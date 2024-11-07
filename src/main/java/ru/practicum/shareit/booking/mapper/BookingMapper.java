package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public BookingDto toDto(Booking booking) {
        if (booking == null) return null;

        return BookingDto.builder()
                .id(booking.getId())
                .bookingStart(booking.getBookingStart())
                .bookingEnd(booking.getBookingEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .bookingStatus(booking.getBookingStatus())
                .build();
    }

    public Booking toBooking(BookingDto bookingDto) {
        if (bookingDto == null) return null;

        return Booking.builder()
                .id(bookingDto.getId())
                .bookingStart(bookingDto.getBookingStart())
                .bookingEnd(bookingDto.getBookingEnd())
                .item(bookingDto.getItem())
                .booker(bookingDto.getBooker())
                .bookingStatus(bookingDto.getBookingStatus())
                .build();
    }

    public Collection<BookingDto> toDtos(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Collection<Booking> toBookings(List<BookingDto> dtos) {
        return dtos.stream()
                .map(this::toBooking)
                .collect(Collectors.toList());
    }

    public Booking updateBookingFromDto(BookingDto bookingDto, Booking booking) {
        if (bookingDto.getBookingStart() != null) booking.setBookingStart(bookingDto.getBookingStart());
        if (bookingDto.getBookingEnd() != null) booking.setBookingEnd(bookingDto.getBookingEnd());
        if (bookingDto.getItem() != null) booking.setItem(bookingDto.getItem());
        if (bookingDto.getBookingStatus() != null) booking.setBookingStatus(bookingDto.getBookingStatus());

        return booking;
    }
}
