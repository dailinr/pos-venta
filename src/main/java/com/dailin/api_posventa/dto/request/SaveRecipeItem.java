package com.dailin.api_posventa.dto.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record SaveRecipeItem(
    
    @Positive(message = "dish_id debería ser un número positivo")
    @JsonProperty(value = "dish_id")
    Long dishId,

    @Positive(message = "ingredient_id debería ser un número positivo")
    @JsonProperty(value = "ingredient_id")
    Long productId,

    @Min(value = 1, message = "{generic.min}")
    int quantity

) implements Serializable { }