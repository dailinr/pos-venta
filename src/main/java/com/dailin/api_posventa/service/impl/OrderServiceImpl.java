package com.dailin.api_posventa.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dailin.api_posventa.dto.request.SaveOrder;
import com.dailin.api_posventa.dto.request.SaveOrderItem;
import com.dailin.api_posventa.dto.response.GetDish;
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
import com.dailin.api_posventa.service.RecipeItemService;
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

    @Autowired
    private RecipeItemService recipeItemService;

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
        
        // Buscar la mesa asociada a esta order
        Order oldOrder = this.findOneEntityById(id); // valida que la order exista
        DiningTable oldTable = oldOrder.getTable();
        
        // liberarla y persistir la entidad
        oldTable.setState(TableState.LIBRE);
        tableService.save(oldTable);

        // eliminar order y sus items de la base de datos
        orderCrudRepository.deleteById(id);
    }

    // --- Lógica Auxiliar para Ítem de Orden ---
    private OrderItem processOrderItem(SaveOrderItem itemDto, Order parentOrder) {
        
        // Usamos el DTO del mapper para obtener la estructura base del ítem
        OrderItem orderItem = OrderItemMapper.toEntity(itemDto);

        // Validar la restriccion XOR 
        boolean hasDish = itemDto.dishId() != null;
        boolean hasProduct = itemDto.productId() != null;

        if(hasDish == hasProduct){ // si ambos son true (1+1=2) o ambos false (0+0=0)
            throw new RuntimeException(
                "Un ítem de una orden debe ser al menos un Plato o un Producto, pero no ambos."
            );
        }
        
        double price = 0.0;
        
        // B. Asignar Plato (si existe) y obtener precio
        if (hasDish) {
            Dish dish = dishService.findOneEntityById(itemDto.dishId()); // Asume findOneEntityById existe

            // extraer el listado de ingredientes del plato
            List<GetDish.GetRecipeItem> ingredients = recipeItemService.findAllByDishId(dish.getId(), Pageable.unpaged()).getContent();

            // procesar y disminuir el stock de los ingredientes
            this.processDishStock(ingredients, itemDto.quantity(), dish.getName());

            orderItem.setDish(dish);
            price = dish.getPrice();
        } 
        
        // C. Asignar Producto (si existe) y obtener precio
        if (hasProduct) {
            Product product = productService.findOneEntityById(itemDto.productId()); // Asume findOneEntityById existe

            String errorMessage = String.format(
                "Stock insuficiente. Solo hay %d %s disponibles del producto '%s'.",
                product.getQuantityAvailable(), product.getMeasureUnit(), product.getName()
            );

            // procesar y disminuir el stock del producto
            productService.decreaseStock(product, itemDto.quantity(), errorMessage);

            orderItem.setProduct(product); // el item de la order será del tipo Product
            price = product.getPrice(); // guardamos el precio
        }

        // D. Asignar propiedades calculadas y FK
        orderItem.setUnitPrice(price);
        orderItem.setSubtotal(price * itemDto.quantity());
        orderItem.setOrder(parentOrder);
        
        // los OrderItem se guardan automaticamenet ya que  Order tiene CascadeType.ALL
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

                // reponer el stock (si el item es eliminado)
                this.revertStock(existingItem);
            } else {
                // Si el ítem existente SÍ está en el DTO, actualizar la cantidad
                int oldQuantity = existingItem.getQuantity(); // guardamos la cantidad antigua

                OrderItemMapper.updateEntity(matchingDto, existingItem); 

                int newQuantity = existingItem.getQuantity(); // obtenemos la nueva antidad

                // Ajustar Stock (si la cantidad cambia)
                this.adjustStockForUpdate(existingItem, oldQuantity, newQuantity);

                // Recalcular precio/subtotal después de actualizar la cantidad
                recalculateOrderItem(existingItem);
            }
        });

        // Remover los ítems marcados. 'orphanRemoval=true' se encarga de borrarlos de la DB.
        order.getOrderItems().removeAll(itemsToRemove);

        // 2. Identificar ítems para AGREGAR
        // Crear un Set con las claves únicas de los ítems que YA existen/fueron actualizados
        Set<String> existingKeys = order.getOrderItems().stream()
            .map(item -> 
                (item.getDish() != null ? item.getDish().getId() : "0") + 
                "_" + 
                (item.getProduct() != null ? item.getProduct().getId() : "0")
            ).collect(Collectors.toSet());

        newItemsDto.forEach(dto -> {
            String newKey = (dto.dishId() != null ? dto.dishId() : "0") + "_" + (dto.productId() != null ? dto.productId() : "0");
            
            // Verificación rápida O(1)
            if (!existingKeys.contains(newKey)) {
                // Si el DTO no coincide con ningún ítem existente, AGREGAR como nuevo
                OrderItem newItem = this.processOrderItem(dto, order); 
                order.getOrderItems().add(newItem); 
            }
        });
    }

    // Este método maneja el aumento o disminución de la cantidad de un ítem existente.
    private void adjustStockForUpdate(OrderItem item, int oldQuantity, int newQuantity) {
        
        if(oldQuantity == newQuantity){
            return; // no se realiza ningún cambio
        }

        int stockChange = newQuantity - oldQuantity;

        // Si la cantidad es positiva, se necesita más stock (disminución)
        if (stockChange > 0) {
            int quantityToDecrease = stockChange;
            
            if (item.getProduct() != null) {
                // Es un Producto directo
                Product product = productService.findOneEntityById(item.getProduct().getId());

                String errorMessage = String.format(
                    "Stock insuficiente. Solo hay %d %s disponibles del producto '%s'.",
                    product.getQuantityAvailable(), product.getMeasureUnit(), product.getName()
                );
                productService.decreaseStock(product, quantityToDecrease, errorMessage);
                
            } else if (item.getDish() != null) {
                // Es un Plato
                Dish dish = item.getDish();
                List<GetDish.GetRecipeItem> ingredients = recipeItemService.findAllByDishId(dish.getId(), Pageable.unpaged()).getContent();
                
                // Reutiliza la lógica de plato, pero solo con la cantidad de CAMBIO
                this.processDishStock(ingredients, quantityToDecrease, dish.getName());
            }
            
        } 
        // Si la cantidad es negativa, se libera stock (reversión)
        else if (stockChange < 0) {
            int quantityToIncrease = Math.abs(stockChange); 
            this.increaseStock(item, quantityToIncrease); // Creamos un nuevo método para la reposición
        }

    }

    /**
     * Aumenta el stock cuando se elimina un item de la orden o se reduce su cantidad.
     * @param item El OrderItem (existente) afectado.
     * @param quantityToIncrease La cantidad de stock a reponer.
     */
    private void increaseStock(OrderItem item, int quantityToIncrease) {
        if (item.getProduct() != null) {
            // Es un Producto directo
            Product product = productService.findOneEntityById(item.getProduct().getId());
            product.setQuantityAvailable(product.getQuantityAvailable() + quantityToIncrease);
            productService.save(product);
            
        } else if (item.getDish() != null) {
            // Es un Plato: Reponer stock de los ingredientes
            Dish dish = item.getDish();
            List<GetDish.GetRecipeItem> ingredients = recipeItemService.findAllByDishId(dish.getId(), Pageable.unpaged()).getContent();

            for(GetDish.GetRecipeItem ingredient : ingredients) {
                int stockToIncrease = ingredient.quantity() * quantityToIncrease;
                Product product = productService.findOneEntityById(ingredient.productId());
                product.setQuantityAvailable(product.getQuantityAvailable() + stockToIncrease);
                productService.save(product);
            }
        }
    }

    /**
     * Llama a increaseStock para reponer completamente el stock de un ítem marcado para eliminación.
     * @param item El OrderItem que está siendo eliminado.
     */
    private void revertStock(OrderItem item) {
        this.increaseStock(item, item.getQuantity());
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

    /**
     * Procesa la disminución de stock de los productos que componen un plato.
     * @param ingredients Los ítems de la receta (productos/ingredientes)
     * @param dishQuantity La cantidad de platos pedidos en la orden
     * @param dishName Nombre del plato para mensajes de error
     */
    private void processDishStock(
        List<GetDish.GetRecipeItem> ingredients,
        int dishQuantity, String dishName
    ){

        for(GetDish.GetRecipeItem ingredient : ingredients) {

            // cantida total requerida para ese ingrediente (Cantidad requerida por un plato) * (Cantidad de platos pedidos)
            int requiredStock = ingredient.quantity() * dishQuantity;

            // Obtener el producto como entidad
            Product product = productService.findOneEntityById(ingredient.productId());

            String errorMessage = String.format(
                "Stock insuficiente. Para el plato '%s' se requieren %d %s de '%s', pero solo hay %d %s disponibles.",
                dishName, requiredStock, product.getMeasureUnit(), product.getName(), product.getQuantityAvailable(), product.getMeasureUnit()
            );

            productService.decreaseStock(product, requiredStock, errorMessage);
        }
    }
}