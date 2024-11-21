package ru.practicum.shareit.booking.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.exception.NoSuchBookingException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component("MemoryBookingStorage")
public class MemoryBookingStorage implements BookingStorage {
    HashMap<Long, Booking> storage = new HashMap<>();

    @Override
    public Booking save(Booking booking) {
        log.debug("Adding booking for user " + booking.getBooker());
        booking.setId(generateId());
        return storage.put(booking.getId(), booking);
    }

    @Override
    public Booking update(Booking booking) {
        log.debug("Updating booking " + booking.getId());
        find(booking.getId());
        return storage.put(booking.getId(), booking);
    }

    @Override
    public Booking delete(Long id) {
        log.debug("Deleting booking " + id);
        return Optional.ofNullable(storage.remove(id)).orElseThrow(() -> {
            log.warn("Incorrect booking id: " + id);
            throw new NoSuchBookingException("Incorrect id");
        });
    }

    @Override
    public Booking find(Long id) {
        log.debug("Getting booking " + id);
        return Optional.ofNullable(storage.get(id)).orElseThrow(() -> {
            log.warn("Incorrect booking id: " + id);
            throw new NoSuchBookingException("Incorrect id");
        });
    }

    @Override
    public Collection<Booking> findByBooker(Long id) {
        log.debug("Getting bookings of user " + id);
        return storage.values().stream()
                .filter(booking -> booking.getBooker().getId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Booking> findAll() {
        log.debug("Requesting all bookings");
        return storage.values();
    }

    private long generateId() {
        log.debug("Generating id...");
        long currId = storage.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currId;
    }
}