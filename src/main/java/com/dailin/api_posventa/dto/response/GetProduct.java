package com.dailin.api_posventa.dto.response;

import java.io.Serializable;

import com.dailin.api_posventa.utils.CategoryType;
import com.dailin.api_posventa.utils.MeasureUnit;

public record GetProduct(
    
    boolean available, 
    double price, 
    int quantityAvailable, 
    String description, 
    String name, 
    MeasureUnit measureUnit,
    GetCategory category

) implements Serializable {

    public static record GetCategory(
        boolean available, 
        Long id, 
        String name, 
        CategoryType type
    ) implements Serializable {}
    
}