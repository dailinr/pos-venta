package com.dailin.api_posventa.dto.response;

import java.io.Serializable;

import com.dailin.api_posventa.utils.MeasureUnit;

public record GetProduct(
    
    Long id,
    boolean available, 
    double price, 
    int quantityAvailable, 
    String description, 
    String name, 
    MeasureUnit measureUnit,
    String categoryTitle,
    Long categoryId
    // GetCategorySimple category // dto simple y externo

) implements Serializable { 

    public static record GetIngredient(
        Long id, int quantity,
        String dishTitle, Long dishId
    ) implements Serializable {}
}