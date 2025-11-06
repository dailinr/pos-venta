package com.dailin.api_posventa.dto.request;

import com.dailin.api_posventa.utils.CategoryType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record SaveCategory(
    
    String name,
    
    CategoryType type, // Espera un string como "MENU" o "INVENTARIO"

    @JsonProperty(value = "price_enabled")
    boolean priceEnabled,

    @JsonProperty(value = "parent_category_id")
    Long parentCategoryId

) implements Serializable {}