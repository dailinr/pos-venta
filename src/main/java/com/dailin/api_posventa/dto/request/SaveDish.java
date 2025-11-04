package com.dailin.api_posventa.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SaveDish(
    
    boolean available, 

    @Min(value = 1, message = "{generic.min}")
    double price,

    @Size(max = 600, message = "{generic.size.max}")
    String description,
    
    @Size(min = 4, max = 255, message = "{generic.size}")
    @NotBlank(message = "{generic.notblank}")
    String name,

    Long categoryId

) implements Serializable { }