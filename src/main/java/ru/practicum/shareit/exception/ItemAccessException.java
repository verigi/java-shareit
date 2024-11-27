package ru.practicum.shareit.exception;

public class ItemAccessException extends RuntimeException {
    public ItemAccessException(final String msg) {
        super(msg);
    }
}