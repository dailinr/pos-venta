package com.dailin.api_posventa.dto.response;

import java.io.Serializable;

import com.dailin.api_posventa.utils.ServiceType;
import com.dailin.api_posventa.utils.TableState;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GetTable(
    
    Long id,
    int number,
    TableState state,
    
    @JsonProperty(value = "service_type") 
    ServiceType serviceType

) implements Serializable { }