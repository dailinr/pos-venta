package com.dailin.api_posventa.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

// Esta interfaz se usa para mapear directamente los resultados de la consulta nativa
public interface ItemProjection {
    Long getId();
    String getName();
    Double getPrice();
    Boolean getAvailable();
    String getDescription(); 
    
    @JsonProperty("item_type")
    String getItemType(); 
    
    @JsonProperty("category_id")
    Long getCategoryId();
    
    @JsonProperty("category_title")
    String getCategoryTitle();
}