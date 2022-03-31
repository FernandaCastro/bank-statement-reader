package com.fcastro.exception;

public class ParseCSVException extends RuntimeException{

    public ParseCSVException(String message) {
        super("File format does not match the Statement Configuration: " + message);
    }
}
