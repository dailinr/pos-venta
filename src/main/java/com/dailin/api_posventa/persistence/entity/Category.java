package com.dailin.api_posventa.persistence.entity;

import com.dailin.api_posventa.utils.Categories;
import com.dailin.api_posventa.utils.CategoryType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryType type;

    private boolean available;

    private int categoryId;

    // ----- -
    private int productId;

    
    private int dishId;
}