package com.dailin.api_posventa.dto.request;

import java.util.List;

import org.springframework.lang.Nullable;

public record MenuSearchCriteria(
    
    Boolean available,
    String categoryTitle,
    
    @Nullable
    List<Long> categoryIds

) { }