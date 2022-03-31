package com.fcastro.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(Class type, Long id) {
        super("Could not find " + type.getSimpleName() + " " + id);
    }

    public ResourceNotFoundException(Class type, Long clientId, Long bankId) {
        super("Could not find " + type.getSimpleName() + " for Client Id: " + clientId + " and Bank Id: " + bankId);
    }
}
