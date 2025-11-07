package com.dailin.api_posventa.dto.response;

import java.io.Serializable;

public record GetDish(

    Long id,
    boolean available, 
    double price,
    String description, 
    String name,
    // GetCategorySimple category // DTO simple y externo
    String categoryTitle,
    Long categoryId

) implements Serializable { }