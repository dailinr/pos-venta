package com.dailin.api_posventa.dto.request;

import com.dailin.api_posventa.utils.CategoryType;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

public record SaveCategory(
    
    @NotBlank(message = "{generic.notblank}")
    String name,
    
    CategoryType type, // Espera un string como "MENU" o "INVENTARIO"

    @JsonProperty(value = "price_enabled")
    @NotBlank(message = "{generic.notblank}")
    boolean priceEnabled,

    @Positive(message = "parent_category_id debería ser un número positivo")
    @JsonProperty(value = "parent_category_id")
    Long parentCategoryId

) implements Serializable {}