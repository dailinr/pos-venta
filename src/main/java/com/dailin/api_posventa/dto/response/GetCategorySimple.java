package com.dailin.api_posventa.dto.response;

import com.dailin.api_posventa.utils.CategoryType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record GetCategorySimple(

    Long id,
    String name,
    CategoryType type,
    
    @JsonProperty(value = "price_enabled")
    Boolean priceEnabled,
    
    @JsonProperty(value = "parent_category_id")
    Long parentCategoryId,

    @JsonProperty(value = "parent_category_title")
    String parentCategoryTitle

) implements Serializable {}