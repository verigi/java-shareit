package ru.practicum.shareit.exception;

public class NoSuchBookingException extends RuntimeException {
    public NoSuchBookingException(final String msg) {
        super(msg);
    }
}