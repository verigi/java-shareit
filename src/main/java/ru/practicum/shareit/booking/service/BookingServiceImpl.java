package ru.practicum.shareit.booking.service;


import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.CommonChecker;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl extends CommonChecker implements BookingService {
    private BookingRepository bookingRepository;
    private BookingMapper mapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              BookingMapper mapper) {
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public BookingDto saveBooking(Long userId, BookingCreateDto bookingDto) {
        log.debug("Save booking request received. Booker id: {}", userId);
        Item item = checkItemAndReturn(bookingDto.getItemId());
        User user = checkUserAndReturn(userId);

        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        validateBookingDates(start, end);
        validateItemAvailability(userId, item);

        Booking booking = mapper.toBooking(bookingDto, item, user);
        bookingRepository.save(booking);

        log.debug("Saving successful!");
        return mapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long userId, BookingUpdateDto bookingUpdateDto) {
        log.debug("Update booking request received. Booking id: {}", bookingUpdateDto.getId());

        Booking updBooking = checkBookingAndReturn(bookingUpdateDto.getId());
        boolean isBooker = updBooking.getBooker().getId().equals(userId);

        if (!isBooker) {
            log.warn("Access denied: invalid user");
            throw new BookingAccessException("Booker can update booking only");
        }

        // статус
        if (bookingUpdateDto.getStatus() != null) {
            if ((updBooking.getStatus() == Status.WAITING || updBooking.getStatus() == Status.APPROVED) &&
                    bookingUpdateDto.getStatus() == Status.CANCELLED) {
                updBooking.setStatus(Status.CANCELLED);
            } else {
                log.warn("Invalid status update attempt. Current status: {}, new status: {}",
                        updBooking.getStatus(), bookingUpdateDto.getStatus());
                throw new BookingAccessException("Booking can be cancelled in statuses 'WAITING' or 'APPROVED' only");
            }
        }

        // время
        if (bookingUpdateDto.getStart() != null || bookingUpdateDto.getEnd() != null) {
            if (!updBooking.getStatus().equals(Status.WAITING)) {
                log.warn("Invalid time to update booking. Booking id: {}",
                        updBooking.getId());
                throw new BookingAccessException("Booking dates can only be updated in 'WAITING' status only");
            }
            validateBookingDates(bookingUpdateDto.getStart(), bookingUpdateDto.getEnd());
            mapper.updateBookingFromDto(bookingUpdateDto, updBooking);
        }

        bookingRepository.save(updBooking);
        log.debug("Updating successful! Booking id: {}", updBooking.getId());
        return mapper.toDto(updBooking);
    }

    @Override
    @Transactional
    public BookingDto deleteBooking(Long userId, Long bookingId) {
        log.debug("Delete booking request received. Booking id: {}", bookingId);

        Booking booking = checkBookingAndReturn(bookingId);
        boolean isBooker = booking.getBooker().getId().equals(userId);

        if (isBooker) {
            bookingRepository.delete(booking);
            log.debug("Deleting successful!");
        } else {
            log.debug("Check permissions. User is not a booker");
            throw new BookingAccessException("Only the booker can cancel the booking");
        }
        return mapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findBooking(Long bookingId, Long userId) {
        log.debug("Get booking request received. Booking id: {}", bookingId);

        Booking booking = checkBookingAndReturn(bookingId);
        User user = checkUserAndReturn(userId);
        User owner = checkUserAndReturn(booking.getItem().getOwner().getId());

        if (!booking.getBooker().getId().equals(userId) && !owner.getId().equals(userId)) {
            throw new BookingAccessException("Not enough permissions: booker and owner can see booking only");
        }

        return mapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> findAllBookingsByUserAndState(Long userId, String state) {
        log.debug("Get all bookings of user request received. User id {}. State: {}", userId, state);

        State currState = State.valueOf(state);
        User user = checkUserAndReturn(userId);
        Collection<Booking> bookings;

        switch (currState) {
            case ALL -> bookings = bookingRepository.findAllByBookerId(userId);
            case CURRENT -> bookings = bookingRepository.findAllCurrentBookingsByBookerId(userId);
            case PAST -> bookings = bookingRepository.findAllPastBookingByBookerId(userId);
            case FUTURE -> bookings = bookingRepository.findAllFutureBookingByBookerId(userId);
            case WAITING -> bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING);
            case REJECTED -> bookings = bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED);
            default -> throw new NoSuchStateException("Incorrect state");
        }

        return bookings.stream()
                .map(mapper::toDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> findAllBookingsByOwnerAndState(Long userId, String state) {
        log.debug("Get all bookings of owner request received. Owner id: {}. State: {}", userId, state);

        State currState = State.valueOf(state);
        User user = checkUserAndReturn(userId);
        Collection<Booking> bookings;

        switch (currState) {
            case ALL -> bookings = bookingRepository.findAllByOwnerId(userId);
            case CURRENT -> bookings = bookingRepository.findAllCurrentBookingByOwnerId(userId);
            case PAST -> bookings = bookingRepository.findAllPastBookingByOwnerId(userId);
            case FUTURE -> bookings = bookingRepository.findAllFutureBookingByOwnerId(userId);
            case WAITING -> bookings = bookingRepository.findAllByOwnerIdAndStatus(userId, Status.WAITING);
            case REJECTED -> bookings = bookingRepository.findAllByOwnerIdAndStatus(userId, Status.REJECTED);
            default -> throw new NoSuchStateException("Incorrect state");
        }

        return bookings.stream()
                .map(mapper::toDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        log.debug("Approve booking request received. Booking id: {}", bookingId);

        Booking booking = checkBookingAndReturn(bookingId);
        Item item = checkItemAndReturn(booking.getItem().getId());

        if (!item.getOwner().getId().equals(userId)) {
            throw new CommentIncorrectTimeException("Booking can be approved by owner only");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ItemAvailabilityException("Booking can be approved in 'WAITING' status only");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);

        return mapper.toDto(booking);
    }

    private void validateBookingDates(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end) || start.equals(end)) {
            log.warn("Check booking time: start - {}, end - {}", start, end);
            throw new ValidationException("Incorrect booking time: the start must be early than the end");
        }
    }

    private void validateItemAvailability(Long userId, Item item) {
        if (!item.getAvailable()) {
            log.warn("Check availability of item. Item id: {}", item.getId());
            throw new ItemAvailabilityException("Item is not available");
        }
        if (item.getOwner().getId().equals(userId)) {
            log.warn("Check booker. Booker id: {}", userId);
            throw new ItemAvailabilityException("Cannot book own item");
        }
    }
}