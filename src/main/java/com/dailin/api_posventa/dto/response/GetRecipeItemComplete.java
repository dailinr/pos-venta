package com.dailin.api_posventa.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetRecipeItemComplete(

    Long id,

    @JsonProperty(value = "dish_id") Long dishId,

    @JsonProperty(value = "dish_title") String dishTitle,

    @JsonProperty(value = "ingredient_id") Long productId,

    @JsonProperty(value = "ingredient_title")
    String productTitle,

    int quantity

) implements Serializable { }
