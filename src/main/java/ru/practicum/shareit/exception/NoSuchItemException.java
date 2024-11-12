package ru.practicum.shareit.exception;

public class NoSuchItemException extends RuntimeException {
    public NoSuchItemException(final String msg) {
        super(msg);
    }
}