package ru.practicum.shareit.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoSuchBookingException;
import ru.practicum.shareit.exception.NoSuchItemException;
import ru.practicum.shareit.exception.NoSuchRequestException;
import ru.practicum.shareit.exception.NoSuchUserException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class CommonChecker {
    @Autowired
    protected ItemRepository itemRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected BookingRepository bookingRepository;
    @Autowired
    protected RequestRepository requestRepository;


    protected User checkUserAndReturn(Long userId) {
        log.debug("Checking user");

        return userRepository.findById(userId).orElseThrow(() ->
                new NoSuchUserException("Incorrect user id: " + userId));
    }

    protected Item checkItemAndReturn(Long itemId) {
        log.debug("Checking item");

        return itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchItemException("Incorrect user id: " + itemId));
    }

    protected Booking checkBookingAndReturn(Long bookingId) {
        log.debug("Checking booking");

        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NoSuchBookingException("Incorrect booking id: " + bookingId));
    }

    protected Request checkRequestAndReturn(Long requestId) {
        log.debug("Checking request");

        return requestRepository.findById(requestId).orElseThrow(() ->
                new NoSuchRequestException("Incorrect request id: " + requestId));
    }
}