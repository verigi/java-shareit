package ru.practicum.shareit.exception;

public class RequestAccessException extends RuntimeException {
    public RequestAccessException(final String msg) {
        super(msg);
    }
}