package com.traki.trakiapi.models.exceptions;

public class CourierNotFoundException extends RuntimeException {
    public CourierNotFoundException(String message) {
        super(message);
    }
}
