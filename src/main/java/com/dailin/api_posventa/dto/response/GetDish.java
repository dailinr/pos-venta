package com.dailin.api_posventa.dto.response;

import java.io.Serializable;

import com.dailin.api_posventa.utils.CategoryType;

public record GetDish(

    boolean available, 
    double price,
    String description, 
    String name,
    GetCategory category

) implements Serializable {

    // DTO interno para la Categoría
    public static record GetCategory(
        Long id, 
        String name, 
        CategoryType type,
        boolean available
    ) implements Serializable {}
}