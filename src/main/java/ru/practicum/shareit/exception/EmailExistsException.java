package ru.practicum.shareit.exception;

public class EmailExistsException extends RuntimeException{
    public EmailExistsException(final String msg){
        super(msg);
    }
}
