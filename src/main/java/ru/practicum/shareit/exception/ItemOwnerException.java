package ru.practicum.shareit.exception;

public class ItemOwnerException extends RuntimeException {
    public ItemOwnerException(final String msg){
        super(msg);
    }
}
