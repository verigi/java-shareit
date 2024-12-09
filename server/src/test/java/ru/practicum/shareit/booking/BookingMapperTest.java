package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class BookingMapperTest {

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BookingMapper bookingMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Booking to DTO")
    void toDto_shouldMapBookingToBookingDto() {
        User user = new User(1L, "User", "user@yandex.ru");
        Item item = new Item(1L, "Item Name", "Item Description", true, user, null);
        LocalDateTime testTime = LocalDateTime.of(2024, 12, 5, 15, 34, 0);
        Booking booking = Booking.builder()
                .id(1L)
                .start(testTime)
                .end(testTime.plusDays(1))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        ItemDto itemDto = new ItemDto(1L, "Item Name", "Item Description", true, 1L, null);
        UserDto userDto = new UserDto(1L, "User", "user@yandex.ru");

        when(itemMapper.toDto(item)).thenReturn(itemDto);
        when(userMapper.toDto(user)).thenReturn(userDto);

        BookingDto bookingDto = bookingMapper.toDto(booking);

        assertNotNull(bookingDto);
        assertEquals(1L, bookingDto.getId());
        assertEquals("Item Name", bookingDto.getItem().getName());
        assertEquals("User", bookingDto.getBooker().getName());
        assertEquals(Status.WAITING, bookingDto.getStatus());
    }

    @Test
    @DisplayName("Booking to DTO. Null booking")
    void toDto_shouldReturnNullWhenBookingIsNull() {
        assertNull(bookingMapper.toDto(null));
    }

    @Test
    @DisplayName("BookingCreateDto to Booking")
    void toBooking_shouldMapBookingCreateDtoToBooking() {
        Item item = new Item(1L, "Item Name", "Item Description", true, null, null);
        User user = new User(1L, "User", "user@yandex.ru");
        LocalDateTime testTime = LocalDateTime.of(2024, 12, 5, 15, 34, 0);

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(testTime.plusHours(1))
                .end(testTime.plusHours(2))
                .itemId(1L)
                .bookerId(1L)
                .build();

        Booking booking = bookingMapper.toBooking(bookingCreateDto, item, user);

        assertNotNull(booking);
        assertEquals(bookingCreateDto.getStart(), booking.getStart());
        assertEquals(bookingCreateDto.getEnd(), booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(user, booking.getBooker());
        assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    @DisplayName("BookingCreateDto to Booking. Null DTO")
    void toBooking_shouldReturnNullWhenBookingCreateDtoIsNull() {
        assertNull(bookingMapper.toBooking(null, null, null));
    }

    @Test
    @DisplayName("Booking update from DTO")
    void updateBookingFromDto_shouldUpdateBookingFields() {
        User user = new User(1L, "User", "user@yandex.ru");
        Item item = new Item(1L, "Item Name", "Item Description", true, user, null);
        LocalDateTime testTime = LocalDateTime.of(2024, 12, 5, 15, 34, 0);

        Booking booking = Booking.builder()
                .id(1L)
                .start(testTime)
                .end(testTime.plusDays(1))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        BookingUpdateDto bookingUpdateDto = BookingUpdateDto.builder()
                .start(testTime.plusHours(2))
                .end(testTime.plusHours(3))
                .status(Status.WAITING)
                .build();

        bookingMapper.updateBookingFromDto(bookingUpdateDto, booking);

        assertEquals(testTime.plusHours(2), booking.getStart());
        assertEquals(testTime.plusHours(3), booking.getEnd());
        assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    @DisplayName("Booking update from DTO. Null DTO")
    void updateBookingFromDto_shouldNotUpdateWhenNull() {
        LocalDateTime testTime = LocalDateTime.of(2024, 12, 5, 15, 34, 0);

        Booking booking = Booking.builder()
                .id(1L)
                .start(testTime)
                .end(testTime.plusDays(1))
                .status(Status.WAITING)
                .build();

        bookingMapper.updateBookingFromDto(null, booking);

        assertEquals(Status.WAITING, booking.getStatus());
    }
}