package ru.practicum.shareit.exception;

public class CommentIncorrectTimeException extends RuntimeException {
    public CommentIncorrectTimeException(final String msg) {
        super(msg);
    }
}