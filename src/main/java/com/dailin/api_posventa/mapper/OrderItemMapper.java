package com.dailin.api_posventa.mapper;

import com.dailin.api_posventa.dto.request.SaveOrderItem;
import com.dailin.api_posventa.dto.response.GetOrderItem;
import com.dailin.api_posventa.persistence.entity.Dish;
import com.dailin.api_posventa.persistence.entity.OrderItem;
import com.dailin.api_posventa.persistence.entity.Product;


public class OrderItemMapper {
    
    public static GetOrderItem toGetDto(OrderItem entity) {
        if(entity == null) return null;

        GetOrderItem.GetProduct product = entity.getProduct() != null
            ? toGetProductOrderItemDto(entity.getProduct()) : null;

        GetOrderItem.GetDish dish = entity.getDish() != null
            ? toGetDishOrderItemDto(entity.getDish()) : null;


        return new GetOrderItem(
            entity.getId(),
            entity.getQuantity(),
            entity.getUnitPrice(),
            entity.getSubtotal(),
            entity.getCreatedAt(),
            product,
            dish
        );
    }

    public static GetOrderItem.GetProduct toGetProductOrderItemDto(Product entity){
        if(entity == null) return null;

        return new GetOrderItem.GetProduct(
            entity.getId(), 
            entity.getName(), 
            entity.getPrice()
        );
    }

    public static GetOrderItem.GetDish toGetDishOrderItemDto(Dish entity){
        if(entity == null) return null;

        return new GetOrderItem.GetDish(
            entity.getId(), 
            entity.getName(), 
            entity.getPrice()
        );
    }

    public static OrderItem toEntity(SaveOrderItem dto){
        if(dto == null) return null;

        OrderItem newOrderItem = new OrderItem();
        newOrderItem.setQuantity(dto.quantity());

        return newOrderItem;
    }

    public static void updateEntity(
        SaveOrderItem dto, OrderItem entity
    ){
        if(dto == null || entity == null) return;

        entity.setQuantity(dto.quantity());
    }
}