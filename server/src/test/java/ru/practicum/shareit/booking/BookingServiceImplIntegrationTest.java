package ru.practicum.shareit.booking;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@ActiveProfiles("test")
public class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Save booking. Successful save")
    void shouldSaveBookingSuccessfully() {
        User user = userRepository.save(new User(null, "User", "user@yandex.ru"));
        User owner = userRepository.save(new User(null, "Owner", "owner@yandex.ru"));
        Item item = itemRepository.save(Item.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build());

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .build();

        BookingDto savedBooking = bookingService.saveBooking(user.getId(), bookingCreateDto);

        assertNotNull(savedBooking);
        assertEquals("Item Name", savedBooking.getItem().getName());
        assertEquals(user.getId(), savedBooking.getBooker().getId());
    }

    @Test
    @DisplayName("Update booking. Successful update to cancelled")
    void shouldUpdateBookingToCancelledSuccessfully() {
        User user = userRepository.save(new User(null, "User", "user@yandex.ru"));
        User owner = userRepository.save(new User(null, "Owner", "owner@yandex.ru"));
        Item item = itemRepository.save(Item.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build());

        BookingUpdateDto bookingUpdateDto = BookingUpdateDto.builder()
                .id(booking.getId())
                .status(Status.CANCELLED)
                .build();

        BookingDto updatedBooking = bookingService.updateBooking(user.getId(), bookingUpdateDto);

        assertNotNull(updatedBooking);
        assertEquals(Status.CANCELLED, updatedBooking.getStatus());
    }

    @Test
    @DisplayName("Delete booking. Successful delete")
    void shouldDeleteBookingSuccessfully() {
        User user = userRepository.save(new User(null, "User", "user@yandex.ru"));
        User owner = userRepository.save(new User(null, "Owner", "owner@yandex.ru"));
        Item item = itemRepository.save(Item.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build());

        BookingDto deletedBooking = bookingService.deleteBooking(user.getId(), booking.getId());

        assertNotNull(deletedBooking);
        assertEquals(booking.getId(), deletedBooking.getId());
        assertTrue(bookingRepository.findById(booking.getId()).isEmpty());
    }

    @Test
    @DisplayName("Find booking by ID. Successfully found")
    void shouldFindBookingByIdSuccessfully() {
        User user = userRepository.save(new User(null, "User", "user@yandex.ru"));
        User owner = userRepository.save(new User(null, "Owner", "owner@yandex.ru"));
        Item item = itemRepository.save(Item.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build());

        BookingDto foundBooking = bookingService.findBooking(booking.getId(), user.getId());

        assertNotNull(foundBooking);
        assertEquals("Item Name", foundBooking.getItem().getName());
    }

    @Test
    @DisplayName("Find all bookings by user. Successfully found")
    void shouldFindAllBookingsByUserSuccessfully() {
        User user = userRepository.save(new User(null, "User", "user@yandex.ru"));
        User owner = userRepository.save(new User(null, "Owner", "owner@yandex.ru"));
        Item item = itemRepository.save(Item.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build());

        bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build());

        Collection<BookingDto> bookings = bookingService.findAllBookingsByUserAndState(user.getId(), "ALL");

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
    }

    @Test
    @DisplayName("Approve booking. Successful approve")
    void shouldApproveBookingSuccessfully() {
        User owner = userRepository.save(new User(null, "Owner", "owner@yandex.ru"));
        User user = userRepository.save(new User(null, "User", "user@yandex.ru"));
        Item item = itemRepository.save(Item.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build());

        Booking booking = bookingRepository.save(Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build());

        BookingDto approvedBooking = bookingService.approveBooking(booking.getId(), owner.getId(), true);

        assertNotNull(approvedBooking);
        assertEquals(Status.APPROVED, approvedBooking.getStatus());
    }
}