package com.dailin.api_posventa.dto.response;

import java.io.Serializable;

public record GetRecipeItemComplete(

    Long id,

    Long dishId,

    String dishTitle,

    Long productId,

    String productTitle,

    int quantity

) implements Serializable { }
