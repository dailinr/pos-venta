package com.dailin.api_posventa.dto.response;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GetItem(
    Long id,
    String name,
    double price,
    boolean available,
    String description,
    
    // @JsonProperty(value = "item_type")
    // String itemType, // Para indicar si es "DISH" o "PRODUCT"
    
    @JsonProperty(value = "category_id")
    Long categoryId,
    
    @JsonProperty(value = "category_title")
    String categoryTitle
    
    // Aquí puedes añadir más campos comunes o un campo genérico para datos específicos
) implements Serializable {}