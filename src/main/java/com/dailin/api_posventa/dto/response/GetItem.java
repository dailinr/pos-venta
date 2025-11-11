package com.dailin.api_posventa.dto.response;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GetItem(
    Long id,
    String name,
    double price,
    Boolean available,
    String description,
    
    @JsonProperty(value = "item_type")
    String itemType, 
    
    @JsonProperty(value = "category_id")
    Long categoryId,
    
    @JsonProperty(value = "category_title")
    String categoryTitle
    
) implements Serializable {}