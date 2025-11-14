package com.dailin.api_posventa.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

// No incluimos el OrderId aquí para evitar redundancia (será incluido dentro del OrderResponse)
public record GetOrderItem(
    Long id,
    int quantity,

    @JsonProperty(value = "unit_price")
    double unitPrice,
    double subtotal,

    @JsonProperty(value = "create_at")
    LocalDateTime createdAt,
    
    // Información del plato o producto (solo se usa uno de los dos)
    GetProduct product,
    GetDish dish       
    
) implements Serializable {

    public static record GetProduct(
        Long id,
        String name,
        double price
    ) implements Serializable {}

    public static record GetDish (
        Long id,
        String name,
        double price
    ) implements Serializable { }
}