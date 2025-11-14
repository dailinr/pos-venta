package com.dailin.api_posventa.mapper;

import java.util.stream.Collectors;

import com.dailin.api_posventa.dto.request.SaveOrder;
import com.dailin.api_posventa.dto.response.GetOrder;
import com.dailin.api_posventa.persistence.entity.Order;
import com.dailin.api_posventa.persistence.entity.DiningTable; // Importa la entidad Table

public class OrderMapper {
    
    // --- Mapeo de Entidad a DTO de Respuesta (GET) ---
    public static GetOrder toGetDto(Order entity) {

        if(entity == null) return null;

        GetOrder.GetTable table = entity.getTable() != null
            ? toGetTableOrderDto(entity.getTable()) : null;

        return new GetOrder(
            entity.getId(), 
            entity.getState().toString(), // Convertir Enum a String (si tu DTO es String)
            entity.getTotal(), 
            entity.getCreatedAt(), 
            table,
            
            // Mapear la lista de entidades OrderItem a DTOs GetOrderItem
            entity.getOrderItems().stream()
                .map(OrderItemMapper::toGetDto)
                .collect(Collectors.toList())
        );
    }

    public static GetOrder.GetTable toGetTableOrderDto(DiningTable entity){
        if(entity == null) return null;

        return new GetOrder.GetTable(
            entity.getId(), 
            entity.getNumber(), 
            entity.getServiceType()
        );

    }

    // --- Mapeo de DTO de Solicitud a Entidad (POST) ---
    public static Order toEntity(
        SaveOrder dto, DiningTable table
    ) {
        if(dto == null) return null;

        Order newOrder = new Order();
        newOrder.setTable(table); 
        
        return newOrder;
    }

    public static void updateEntity(
        Order entity, DiningTable table
    ) {
        if(entity == null) return;

        entity.setTable(table);
    }
}