package com.dailin.api_posventa.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetOrder(
    Long id,
    String state, // El estado de la orden (ej: "EN_PREPARACION")
    double total,

    @JsonProperty(value = "created_at")
    LocalDateTime createdAt,
    
    // Información de la Mesa
    @JsonProperty(value = "table_id")
    Long tableId, // Un DTO simple para la mesa

    // String table_name;
    
    // La lista de todos los ítems de la orden
    List<GetOrderItem> orderItems 

) implements Serializable { }