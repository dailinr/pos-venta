package com.dailin.api_posventa.dto.request;

public record StockSearchCriteria(
    
    Boolean available,
    String categoryTitle,
    String categoryType

) { }