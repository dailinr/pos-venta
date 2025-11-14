package com.dailin.api_posventa.service.impl;

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
    public Page<GetOrder> findAll(Pageable pageable) {
        Page<Order> entities = orderCrudRepository.findAll(pageable);
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatedOneById'");
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
}