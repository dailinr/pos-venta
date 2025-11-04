package com.dailin.api_posventa.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(

    int httpCode,
    String url,
    String httpMethod,
    String message,
    String backendMessage,
    LocalDateTime timestamp,
    List<String> details

) { }