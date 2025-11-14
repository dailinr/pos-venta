package com.dailin.api_posventa.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Usamos esta clase para representar cada ítem dentro de la orden
public record SaveOrderItem(
    
    @JsonProperty(value = "product_id")
    Long productId, // opcional

    @JsonProperty(value = "dish_id")
    Long dishId, // opcional

    // Cantidad requerida, debe ser al menos 1
    @NotNull(message = "{generic.notnull}")
    @Min(value = 1, message = "{generic.min}")
    int quantity
) {}