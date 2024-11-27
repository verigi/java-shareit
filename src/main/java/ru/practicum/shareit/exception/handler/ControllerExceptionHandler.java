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
    @ExceptionHandler(BookingAccessException.class)
    public ResponseEntity<String> handleBookingAccessException(BookingAccessException e) {
        log.error("Booking access denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(ItemAccessException.class)
    public ResponseEntity<String> handleItemAccessException(ItemAccessException e) {
        log.error("Item access denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailExistsException(EmailExistsException e) {
        log.error("Existing email exception");
        HashMap<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(CommentIncorrectTimeException.class)
    public ResponseEntity<Map<String, String>> handleCommentIncorrectTimeException(CommentIncorrectTimeException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Access denied");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ItemAvailabilityException.class)
    public ResponseEntity<String> handleItemAvailabilityException(ItemAvailabilityException e) {
        log.error("Item is not available");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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

    @ExceptionHandler(NoSuchStateException.class)
    public ResponseEntity<String> noSuchStateException(NoSuchStateException e) {
        log.error("No such state enum exception");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(NoSuchUserException.class)
    public ResponseEntity<String> noSuchUserException(NoSuchUserException e) {
        log.error("No such user exception");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}