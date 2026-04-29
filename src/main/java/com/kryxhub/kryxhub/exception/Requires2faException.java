package com.kryxhub.kryxhub.exception;

public class Requires2faException extends RuntimeException {
    public Requires2faException(String message) {
        super(message);
    }
}