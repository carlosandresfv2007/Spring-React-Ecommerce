package com.ecommerce.project.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ApiError {
    private int status;
    private String message;
    private String path;
    private Map<String, String> errors;
    private LocalDateTime timestamp;
}

