package com.dailin.api_posventa.dto.request;

import java.io.Serializable;

import com.dailin.api_posventa.utils.MeasureUnit;

public record SaveProduct(

    boolean available, 
    double price, 
    int quantityAvailable, 
    String description, 
    String name, 
    MeasureUnit measureUnit,
    Long categoryId

) implements Serializable { }