package com.dailin.api_posventa.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.api_posventa.dto.request.SaveOrder;
import com.dailin.api_posventa.dto.request.SaveOrderItem;
import com.dailin.api_posventa.dto.response.GetOrder;
import com.dailin.api_posventa.exception.ObjectNotFoundException;
import com.dailin.api_posventa.mapper.OrderItemMapper;
import com.dailin.api_posventa.mapper.OrderMapper;
import com.dailin.api_posventa.persistence.entity.DiningTable;
import com.dailin.api_posventa.persistence.entity.Dish;
import com.dailin.api_posventa.persistence.entity.Order;
import com.dailin.api_posventa.persistence.entity.OrderItem;
import com.dailin.api_posventa.persistence.entity.Product;
import com.dailin.api_posventa.persistence.repository.OrderCrudRepository;
import com.dailin.api_posventa.persistence.specification.FindAllOrderSpecification;
import com.dailin.api_posventa.service.DishService;
import com.dailin.api_posventa.service.OrderService;
import com.dailin.api_posventa.service.ProductService;
import com.dailin.api_posventa.service.TableService;
import com.dailin.api_posventa.utils.OrderState;
import com.dailin.api_posventa.utils.TableState;

@Transactional
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderCrudRepository orderCrudRepository;

    @Autowired
    private TableService tableService;

    @Autowired
    private ProductService productService;

    @Autowired
    private DishService dishService;

    @Transactional(readOnly = true)
    @Override
    public Page<GetOrder> findAll(OrderState state, String date, Pageable pageable) {

        FindAllOrderSpecification specification = new FindAllOrderSpecification(state, date);
        Page<Order> entities = orderCrudRepository.findAll(specification, pageable);
        return entities.map(OrderMapper::toGetDto);
    }

    @Transactional(readOnly = true)
    @Override
    public GetOrder findOneById(Long id) {
        return OrderMapper.toGetDto(this.findOneEntityById(id));
    }

    @Transactional(readOnly = true)
    @Override
    public Order findOneEntityById(Long id) {
        return orderCrudRepository.findById(id)
            .orElseThrow(() -> new ObjectNotFoundException("order "+ Long.toString(id)));
    }

    @Transactional(readOnly = true)
    @Override
    public GetOrder findOrderByTableId(Long tableId){
        tableService.findOneEntityById(tableId); // validar que la mesa exista

        return OrderMapper.toGetDto(orderCrudRepository.findByTableId(tableId));
    }

    @Override
    public GetOrder createOne(SaveOrder dto) {
        
        // 1. Obtener la entidad padre y validar la mesa
        DiningTable tableEntity = tableService.findOneEntityById(dto.tableId());

        // cambiamos el estado de la mesa
        if(tableEntity.getState().equals(TableState.LIBRE)) {
            tableEntity.setState(TableState.OCUPADA);
            // persistir el cambio inmediatamente
            tableService.save(tableEntity);
        } else {
            throw new RuntimeException("La mesa "+Long.toString(tableEntity.getNumber())+" no está disponible.");
        }

        // 2. Mapear DTO a entidad Order (asigna la mesa y estado inicial)
        Order newOrder = OrderMapper.toEntity(dto, tableEntity);
        newOrder.setState(OrderState.EN_PREPARACION);
        newOrder.setTotal(0.0);

        // 3. Persistir la orden para obtener su id - para luego persistir sus OrderItems
        Order savedOrder = orderCrudRepository.save(newOrder);

        // Lista para contener los items de la orden
        List<OrderItem> orderItems = dto.orderItems().stream()
            .map(itemDto -> this.processOrderItem(itemDto, savedOrder))
            .collect(Collectors.toList());

        // 4. Asignar los items a la orden
        savedOrder.setOrderItems(orderItems);

        // 5. Calcular el total final de la orden
        double finalTotal = orderItems.stream()
            .mapToDouble(OrderItem::getSubtotal)
            .sum();

        savedOrder.setTotal(finalTotal);

        // 6. Persistir la orden padre con los items y el total (JPA lo guarda por cascada)
        Order finalOrder = orderCrudRepository.save(savedOrder);

        return OrderMapper.toGetDto(finalOrder);
    }

    @Override
    public GetOrder updatedOneById(SaveOrder saveDto, Long id) {
        
        // 1. Encontrar la Orden existente 
        Order oldOrder = this.findOneEntityById(id);

        // 2. Manejar la reasignacion de la Mesa (si es diferente)
        if (saveDto.tableId() != null && 
            !saveDto.tableId().equals(oldOrder.getTable().getId())
        ){
            // manejo de estados de ambas mesas
            this.handleTableReassignment(oldOrder, saveDto.tableId());

            // actualizamos el id_table en la entidad oldOrder
            DiningTable newTable = tableService.findOneEntityById(saveDto.tableId());
            OrderMapper.updateEntity(oldOrder, newTable);
        }

        // 3. Sincronizar los OrderItems (al Agregar, Actualizar o Eliminar)
        this.synchronizeOrderItems(oldOrder, saveDto.orderItems());

        // recalcular total 
        double finalTotal = oldOrder.getOrderItems().stream()
            .mapToDouble(OrderItem::getSubtotal)
            .sum();
        
        oldOrder.setTotal(finalTotal);

        // Persistir la Orden (por CascadeType.ALL)
        Order updatedOrder = orderCrudRepository.save(oldOrder);

        return OrderMapper.toGetDto(updatedOrder); // dto de respuesta
    }

    @Override
    public void deleteOneById(Long id) {
        
        if(orderCrudRepository.existsById(id)){
            orderCrudRepository.deleteById(id);
            return;
        }

        throw new ObjectNotFoundException("order: "+ Long.toString(id));
    }

    // --- Lógica Auxiliar para Ítem de Orden ---
    private OrderItem processOrderItem(SaveOrderItem itemDto, Order parentOrder) {
        
        // Usamos el DTO del mapper para obtener la estructura base del ítem
        OrderItem orderItem = OrderItemMapper.toEntity(itemDto);

        // A. Validar la restricción CHECK (Al menos un ID debe estar presente)
        if (itemDto.dishId() == null && itemDto.productId() == null) {
            throw new RuntimeException("Un ítem debe tener asociado un Plato o un Producto.");
        }
        
        double price = 0.0;
        
        // B. Asignar Plato (si existe) y obtener precio
        if (itemDto.dishId() != null) {
            Dish dish = dishService.findOneEntityById(itemDto.dishId()); // Asume findOneEntityById existe
            orderItem.setDish(dish);
            price = dish.getPrice();
        } 
        
        // C. Asignar Producto (si existe) y obtener precio
        if (itemDto.productId() != null) {
            Product product = productService.findOneEntityById(itemDto.productId()); // Asume findOneEntityById existe
            orderItem.setProduct(product);
            
            // Aquí asumimos que si hay ambos, es una combinación y se suma el precio.
            price += product.getPrice(); 
        }

        // D. Asignar propiedades calculadas y FK
        orderItem.setUnitPrice(price);
        orderItem.setSubtotal(price * itemDto.quantity());
        orderItem.setOrder(parentOrder);
        
        // No es necesario guardar el OrderItem individualmente si la relación
        // en Order tiene CascadeType.ALL, pero el mapeo bidireccional es vital.
        
        return orderItem;
    }

    // logica para manejar el cambio de mesa
    private void handleTableReassignment(Order oldOrder, Long newTableId) {
        
        DiningTable oldTable = oldOrder.getTable();

        // la nueva mesa debe estar libre
        DiningTable newTable = tableService.findOneEntityById(newTableId);
        if(!newTable.getState().equals(TableState.LIBRE)){ 
            throw new RuntimeException(
                "La mesa "+ newTable.getNumber() +" no se encuentra disponible."
            );
        }

        // liberar la mesa anterior
        oldTable.setState(TableState.LIBRE);
        tableService.save(oldTable); // persistimos

        // ocupar la nueva mesa de destino
        newTable.setState(TableState.OCUPADA);
        tableService.save(newTable); // persistimos
    }

    // Compara los items existentes con los items recibidos en DTO para sincronizar la lista
    private void synchronizeOrderItems(Order order, List<SaveOrderItem> newItemsDto) {
    
        // Usa un mapa para fácil acceso a los ítems existentes por sus IDs de Producto/Plato. La clave es una combinación de (dishId, productId)
        
        List<OrderItem> itemsToRemove = new ArrayList<>();
        
        // 1. Identificar ítems para ELIMINAR y/o ACTUALIZAR
        order.getOrderItems().forEach(existingItem -> {
            // La clave de un OrderItem es la combinación de IDs de Dish y Product
            Long existingDishId = existingItem.getDish() != null ? existingItem.getDish().getId() : null; // si existe un plato en la order antigua
            Long existingProductId = existingItem.getProduct() != null ? existingItem.getProduct().getId() : null; // si existe un plato en la order antigua
            
            // Buscar el DTO correspondiente en la nueva lista
            SaveOrderItem matchingDto = newItemsDto.stream()
                .filter(dto -> 
                    java.util.Objects.equals(dto.dishId(), existingDishId) && // si de los nuevos datos hay un plato igual en la antigua order
                    java.util.Objects.equals(dto.productId(), existingProductId) // si de los nuevos datos hay un producto igual en la antigua order
                )   // basicamente: si hay una order igual a la que intentas ingresar nueva
                .findFirst()
                .orElse(null);
                
            if (matchingDto == null) {
                // Si el ítem existente NO está en el DTO, marcar para eliminación
                itemsToRemove.add(existingItem); // cuando el cliente desea eliminar un item de la order (?)
            } else {
                // Si el ítem existente SÍ está en el DTO, actualizar la cantidad
                OrderItemMapper.updateEntity(matchingDto, existingItem); 
                // Recalcular precio/subtotal después de actualizar la cantidad
                recalculateOrderItem(existingItem);
            }
        });

        // Remover los ítems marcados. 'orphanRemoval=true' se encarga de borrarlos de la DB.
        order.getOrderItems().removeAll(itemsToRemove);

        // 2. Identificar ítems para AGREGAR
        newItemsDto.forEach(dto -> {
            // Buscar si este nuevo DTO corresponde a un ítem que ya existe (actualizado en I)
            Long newDishId = dto.dishId();
            Long newProductId = dto.productId();
            
            boolean alreadyExists = order.getOrderItems().stream()
                .anyMatch(item -> 
                    java.util.Objects.equals(item.getDish() != null ? item.getDish().getId() : null, newDishId) &&
                    java.util.Objects.equals(item.getProduct() != null ? item.getProduct().getId() : null, newProductId)
                );

            if (!alreadyExists) {
                // Si el DTO no coincide con ningún ítem existente, AGREGAR como nuevo
                OrderItem newItem = this.processOrderItem(dto, order); // Reutiliza la lógica de creación
                order.getOrderItems().add(newItem); // Añadir a la colección (JPA guardará por cascada)
            }
        });
    }

    /**
     * Recalcula el subtotal del ítem de orden después de una actualización de cantidad.
     */
    private void recalculateOrderItem(OrderItem item) {
        // Si la cantidad se actualizó, recalcular el subtotal
        double subtotal = item.getUnitPrice() * item.getQuantity();
        item.setSubtotal(subtotal);
        // Nota: El precio unitario (unitPrice) NO debería cambiar en una simple actualización.
    }
}