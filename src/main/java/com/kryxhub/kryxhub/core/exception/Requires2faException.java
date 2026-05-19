package com.kryxhub.kryxhub.core.exception;

public class Requires2faException extends RuntimeException {
    public Requires2faException(String message) {
        super(message);
    }
}