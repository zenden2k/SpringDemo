package dev.svistunov.springdemo.exception;

public class BaseException extends RuntimeException {
    public BaseException(String message) {
        super(message);
    }
}