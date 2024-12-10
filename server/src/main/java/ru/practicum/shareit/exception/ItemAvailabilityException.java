package ru.practicum.shareit.exception;

public class ItemAvailabilityException extends RuntimeException {
    public ItemAvailabilityException(final String msg) {
        super(msg);
    }
}