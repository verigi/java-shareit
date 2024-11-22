package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;

import java.util.Collection;

public interface BookingService {
    BookingDto saveBooking(Long userId, BookingCreateDto bookingDto);

    BookingDto updateBooking(BookingUpdateDto bookingDto);

    BookingDto deleteBooking(Long bookingId);

    BookingDto findBooking(Long bookingId, Long userId);
    Collection<BookingDto> findAllBookingsByUserAndState(Long userId, String state);
    Collection<BookingDto> findAllBookingsByOwnerAndState(Long userId, String state);
    BookingDto approveBooking(Long bookingId, Long userId, Boolean approved);
}