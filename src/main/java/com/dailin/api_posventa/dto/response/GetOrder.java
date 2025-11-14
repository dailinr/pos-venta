package com.dailin.api_posventa.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.dailin.api_posventa.utils.ServiceType;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GetOrder(
    Long id,
    String state, // El estado de la orden (ej: "EN_PREPARACION")
    double total,

    @JsonProperty(value = "created_at")
    LocalDateTime createdAt,
    
    // Información de la Mesa
    GetTable table,
    
    // La lista de todos los ítems de la orden
    @JsonProperty(value = "order_items")
    List<GetOrderItem> orderItems 

) implements Serializable {

    public static record GetTable(
        Long id,
        int number,
        @JsonProperty(value = "service_type")
        ServiceType serviceType
    ) implements Serializable { }
}