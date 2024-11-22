package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.service.BookingService;


import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    @Autowired
    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookingDto> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingCreateDto booking) {
        log.info("Received POST request to create booking");
        BookingDto createdBooking = service.saveBooking(userId, booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingDto> update(
            @PathVariable Long bookingId,
            @Valid @RequestBody BookingUpdateDto newBooking) {
        log.info("Received PUT request to update booking with ID: {}", bookingId);
        newBooking.setId(bookingId);
        BookingDto updatedBooking = service.updateBooking(newBooking);
        return ResponseEntity.ok(updatedBooking);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> delete(@PathVariable Long bookingId) {
        log.info("Received DELETE request, booking with ID: {}", bookingId);
        service.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> find(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {
        log.info("Received GET request, booking with ID: {}", bookingId);
        BookingDto booking = service.findBooking(bookingId, userId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    public ResponseEntity<Collection<BookingDto>> findAllBookingsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Received GET request, all bookings for user with ID: {}, state: {}", userId, state);
        Collection<BookingDto> bookings = service.findAllBookingsByUserAndState(userId, state);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingDto>> findAllBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Received GET request, all bookings for owner's items, user ID: {}, state: {}", userId, state);
        Collection<BookingDto> bookings = service.findAllBookingsByOwnerAndState(userId, state);
        return ResponseEntity.ok(bookings);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(
            @PathVariable("bookingId") Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "approved", defaultValue = "false") Boolean approved) {
        log.info("Received PATCH request to approve booking with ID: {}, approved: {}", bookingId, approved);
        BookingDto booking = service.approveBooking(bookingId, userId, approved);
        return ResponseEntity.ok(booking);
    }
}