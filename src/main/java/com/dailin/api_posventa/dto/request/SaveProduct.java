package com.dailin.api_posventa.dto.request;

import java.io.Serializable;

import com.dailin.api_posventa.utils.MeasureUnit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SaveProduct(

    boolean available, 

    // @Min(value = 1, message = "{generic.min}")
    // @Nullable
    Double price, 

    @Min(value = 0, message = "{generic.min}")
    int quantityAvailable, 
    
    @Size(max = 600, message = "{generic.size.max}")
    String description, 

    @Size(min = 4, max = 255, message = "{generic.size}")
    @NotBlank(message = "{generic.notblank}")
    String name, 

    @NotNull
    MeasureUnit measureUnit,

    Long categoryId

) implements Serializable { }