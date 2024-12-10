package ru.practicum.shareit.exception;

public class NoSuchUserException extends RuntimeException {
    public NoSuchUserException(final String msg) {
        super(msg);
    }
}
