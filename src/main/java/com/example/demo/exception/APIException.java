package com.example.demo.exception;

public class APIException extends RuntimeException {
    private final Integer status;

    public APIException(Integer status, String message) {
        super(message);
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }
}