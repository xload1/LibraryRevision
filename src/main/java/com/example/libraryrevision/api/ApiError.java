package com.example.libraryrevision.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Value
@Builder
public class ApiError {
    int status;
    String code;
    String message;
    String path;
    Instant timestamp;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    Map<String, String> validation;
}