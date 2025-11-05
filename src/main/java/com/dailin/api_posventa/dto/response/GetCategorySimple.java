package com.dailin.api_posventa.dto.response;

import com.dailin.api_posventa.utils.CategoryType;
import java.io.Serializable;

public record GetCategorySimple(

    Long id,
    String name,
    CategoryType type,
    Boolean priceEnabled,
    Long parentCategoryId

) implements Serializable {}