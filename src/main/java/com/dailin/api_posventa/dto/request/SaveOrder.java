package com.dailin.api_posventa.dto.request;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SaveOrder(
    
    @NotNull(message = "{generic.notnull}")
    @Min(value = 1, message = "{generic.min}")
    @JsonProperty(value = "table_id")
    Long tableId,

    @NotNull(message = "{generic.notnull}")
    @NotEmpty(message = "{generic.notempty}")
    @Valid // Asegura que se validen los campos dentro del DTO SaveOrderItem
    List<SaveOrderItem> orderItems

) implements Serializable {}