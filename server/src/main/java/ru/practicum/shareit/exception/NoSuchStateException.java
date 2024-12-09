package ru.practicum.shareit.exception;

public class NoSuchStateException extends RuntimeException {
    public NoSuchStateException(final String msg) {
        super(msg);
    }
}