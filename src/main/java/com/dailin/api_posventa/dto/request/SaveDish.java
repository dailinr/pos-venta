package com.dailin.api_posventa.dto.request;

import java.io.Serializable;

public record SaveDish(
    
    boolean available, 
    double price,
    String description, 
    String name,
    Long categoryId

) implements Serializable { }