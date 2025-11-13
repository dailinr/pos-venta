package com.dailin.api_posventa.dto.request;

import java.io.Serializable;

import com.dailin.api_posventa.utils.ServiceType;
import com.dailin.api_posventa.utils.TableState;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;

public record SaveTable(

    @Min(value = 1,  message = "{generic.min}")
    int number,

    TableState state,

    @JsonProperty(value = "service_type")
    ServiceType serviceType

) implements Serializable { }
