package com.ecommerce.project.exceptions;

public class BadRequestException extends RuntimeException {

    private final String field;
    private final String value;

    public BadRequestException(String field, String value) {
        super(String.format("Bad request: invalid %s = %s", field, value));
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}

