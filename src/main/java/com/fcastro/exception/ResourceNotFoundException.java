package com.fcastro.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(Class type, Long id) {
        super("Could not find " + type.getSimpleName() + " " + id);
    }
}
