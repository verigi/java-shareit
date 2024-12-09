package ru.practicum.shareit.exception;

public class BookingAccessException extends RuntimeException {
    public BookingAccessException(final String msg) {
        super(msg);
    }
}