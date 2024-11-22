package ru.practicum.shareit.booking.service;


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
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingMapper mapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository,
                              BookingMapper mapper) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }


    @Override
    @Transactional
    public BookingDto saveBooking(Long userId, BookingCreateDto bookingDto) {
        log.debug("Save booking request received. Booker id: {}", userId);
        Item item = checkItemAndReturn(bookingDto.getItemId());
        User user = checkUserAndReturn(userId);

        if (!item.getAvailable()) {
            throw new ItemAvailabilityException("Item is not available");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ItemAvailabilityException("Can not book own item");
        }

        Booking booking = mapper.toBooking(bookingDto, item, user);
        bookingRepository.save(booking);

        log.debug("Saving successful!");
        return mapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto updateBooking(BookingUpdateDto bookingDto) {
        log.debug("Update booking request received. Booking id: {}", bookingDto.getId());

        Booking updBooking = mapper.updateBookingFromDto(bookingDto, checkBookingAndReturn(bookingDto.getId()));
        bookingRepository.save(updBooking);

        log.debug("Updating successful!");
        return mapper.toDto(updBooking);
    }

    @Override
    @Transactional
    public BookingDto deleteBooking(Long bookingId) {
        log.debug("Delete booking request received. Booking id: {}", bookingId);

        Booking booking = checkBookingAndReturn(bookingId);
        bookingRepository.delete(booking);

        log.debug("Deleting successful!");
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
            throw new ItemOwnerException("Not enough permissions: booker and owner can see booking only");
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
    @Transactional(readOnly = true)
    public BookingDto approveBooking(Long bookingId, Long userId, Boolean approved) {
        log.debug("Approve booking request received. Booking id: {}", bookingId);
        Booking booking = checkBookingAndReturn(bookingId);
        Item item = checkItemAndReturn(booking.getItem().getId());

        if (!item.getOwner().getId().equals(userId)) {
            throw new CommentIncorrectTimeException("Booking can be approved by owner only");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ItemAvailabilityException("Booking can be approved in \"WAITING\" status only");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);

        return mapper.toDto(booking);
    }

    private Booking checkBookingAndReturn(Long bookingId) {
        log.debug("Checking booking");

        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchBookingException("Incorrect booking id: " + bookingId));
    }

    private Item checkItemAndReturn(Long itemId) {
        log.debug("Checking item");

        return itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchItemException("Incorrect user id: " + itemId));
    }

    private User checkUserAndReturn(Long userId) {
        log.debug("Checking user");

        return userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("Incorrect user id: " + userId));
    }
}