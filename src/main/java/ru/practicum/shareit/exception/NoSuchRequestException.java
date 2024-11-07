package ru.practicum.shareit.exception;

public class NoSuchRequestException extends RuntimeException {
    public NoSuchRequestException(final String msg) {
        super(msg);
    }
}