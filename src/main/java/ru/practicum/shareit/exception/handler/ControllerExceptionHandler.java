package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.practicum.shareit.exception.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailExistsException(EmailExistsException e) {
        log.error("Existing email exception");
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(ItemOwnerException.class)
    public ResponseEntity<String> handleItemOwnerException(ItemOwnerException e) {
        log.error("Item owner exception");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(NoSuchBookingException.class)
    public ResponseEntity<String> noSuchBookingException(NoSuchBookingException e) {
        log.error("No such booking exception");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(NoSuchItemException.class)
    public ResponseEntity<String> noSuchItemException(NoSuchItemException e) {
        log.error("No such item exception");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(NoSuchRequestException.class)
    public ResponseEntity<String> noSuchRequestException(NoSuchRequestException e) {
        log.error("No such item request exception");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<String> noSuchUserException(NoSuchUserException e) {
        log.error("No such booking exception");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}