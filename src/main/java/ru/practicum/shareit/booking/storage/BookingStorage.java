package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;

import java.util.Collection;

public interface BookingStorage {
    Booking save(Booking booking);
    Booking update(Booking booking);
    Booking delete(Long id);
    Booking find(Long id);
    Collection<Booking> findByBooker(Long id);
    Collection<Booking> findAll();
}
