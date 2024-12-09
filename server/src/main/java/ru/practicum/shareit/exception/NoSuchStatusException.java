package ru.practicum.shareit.exception;

public class NoSuchStatusException extends RuntimeException {
    public NoSuchStatusException(final String msg) {
        super(msg);
    }
}