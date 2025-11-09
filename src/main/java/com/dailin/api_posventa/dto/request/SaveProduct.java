package com.dailin.api_posventa.dto.request;

import java.io.Serializable;

import com.dailin.api_posventa.utils.MeasureUnit;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SaveProduct(
    
    Double price, 

    @Min(value = 0, message = "{generic.min}")
    @JsonProperty(value = "quantity_available")
    int quantityAvailable, 
    
    @Size(max = 600, message = "{generic.size.max}")
    String description, 

    @Size(min = 4, max = 255, message = "{generic.size}")
    @NotBlank(message = "{generic.notblank}")
    String name, 

    @NotNull
    @JsonProperty(value = "measure_unit")
    MeasureUnit measureUnit,

    @Positive(message = "La category_id debería ser un número positivo")
    @JsonProperty(value = "category_id")
    Long categoryId

) implements Serializable { }