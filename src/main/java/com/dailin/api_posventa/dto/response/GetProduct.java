package com.dailin.api_posventa.dto.response;

import java.io.Serializable;

import com.dailin.api_posventa.utils.MeasureUnit;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GetProduct(
    
    Long id,
    boolean available, 
    double price, 
    @JsonProperty(value = "quantity_available") int quantityAvailable, 
    String description, 
    String name, 
    @JsonProperty(value = "measure_unit") MeasureUnit measureUnit,
    @JsonProperty(value = "category_title") String categoryTitle,
    @JsonProperty(value = "category_id") Long categoryId
    // GetCategorySimple category // dto simple y externo

) implements Serializable { 

    public static record GetRecipeItem(
        Long id, int quantity,
        @JsonProperty(value = "dish_title") String dishTitle, 
        @JsonProperty(value = "dish_id") Long dishId
    ) implements Serializable {}
}