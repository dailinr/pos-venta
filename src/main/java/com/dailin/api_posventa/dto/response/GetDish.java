package com.dailin.api_posventa.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetDish(

    Long id,
    boolean available, 
    double price,
    String description, 
    String name,
    // GetCategorySimple category // DTO simple y externo
    @JsonProperty(value = "category_title") String categoryTitle,
    @JsonProperty(value = "category_id") Long categoryId,
    @JsonProperty(value = "total_ingredients") int totalIngredients

) implements Serializable { 

    public static record GetRecipeItem(
        Long id, 
        @JsonProperty(value = "ingredient_id") Long productId, 
        @JsonProperty(value = "ingredient_title") String productTitle, 
        int quantity
    ) implements Serializable { }
}