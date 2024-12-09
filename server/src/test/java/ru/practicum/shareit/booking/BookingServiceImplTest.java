package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Save booking. Successful save")
    void shouldSaveBookingSuccessfully() {
        Long bookerId = 1L;
        Long itemId = 1L;

        User user = User.builder()
                .id(bookerId)
                .name("Booker")
                .email("booker@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(itemId)
                .name("Item Name")
                .available(true)
                .owner(User.builder()
                        .id(2L)
                        .build())
                .build();

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(itemId)
                .build();

        Booking booking = Booking.builder()
                .id(3L)
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.toBooking(any(BookingCreateDto.class), any(Item.class), any(User.class))).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(BookingDto.builder()
                .id(3L)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(Status.WAITING)
                .item(ItemDto.builder().id(itemId).build())
                .booker(UserDto.builder().id(bookerId).build())
                .build());

        BookingDto result = bookingService.saveBooking(bookerId, bookingCreateDto);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(bookingCreateDto.getStart(), result.getStart());
        assertEquals(Status.WAITING, result.getStatus());
        verify(userRepository).findById(bookerId);
        verify(itemRepository).findById(itemId);
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Save booking. Booking own item")
    void shouldFailToSaveBookingOwnItem() {
        Long bookerId = 1L;
        Long itemId = 1L;

        User user = User.builder()
                .id(bookerId)
                .name("Booker")
                .email("booker@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(itemId)
                .name("Item Name")
                .available(true)
                .owner(user)
                .build();

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(itemId)
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemAvailabilityException exception = assertThrows(ItemAvailabilityException.class,
                () -> bookingService.saveBooking(bookerId, bookingCreateDto));

        assertEquals("Cannot book own item", exception.getMessage());
        verify(itemRepository).findById(itemId);
        verify(userRepository).findById(bookerId);
    }

    @Test
    @DisplayName("Save booking. Booking start time is after end time")
    void shouldFailToSaveBookingWithIncorrectDates() {
        Long bookerId = 1L;
        Long itemId = 1L;

        User user = User.builder()
                .id(bookerId)
                .name("Booker")
                .email("booker@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(itemId)
                .name("Item Name")
                .available(true)
                .owner(User.builder()
                        .id(2L)
                        .build())
                .build();

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .itemId(itemId)
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.saveBooking(bookerId, bookingCreateDto));

        assertEquals("Incorrect booking time: the start must be early than the end", exception.getMessage());
        verify(itemRepository).findById(itemId);
        verify(userRepository).findById(bookerId);
    }

    @Test
    @DisplayName("Save booking. User is not found")
    void shouldThrowNoSuchUserExceptionWhenUserNotFound() {
        Long bookerId = 1L;
        Long itemId = 2L;

        BookingCreateDto bookingDto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(Item.builder().id(itemId).build()));

        NoSuchUserException exception = assertThrows(
                NoSuchUserException.class,
                () -> bookingService.saveBooking(bookerId, bookingDto)
        );

        assertEquals("Incorrect user id: " + bookerId, exception.getMessage());

        verify(userRepository).findById(bookerId);
        verify(itemRepository).findById(itemId);
    }

    @Test
    @DisplayName("Save booking. Item is not found")
    void shouldThrowNoSuchItemExceptionWhenItemNotFound() {
        Long bookerId = 1L;
        Long itemId = 2L;

        BookingCreateDto bookingDto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NoSuchItemException exception = assertThrows(
                NoSuchItemException.class,
                () -> bookingService.saveBooking(bookerId, bookingDto)
        );

        assertEquals("Incorrect user id: " + itemId, exception.getMessage());

        verify(itemRepository).findById(itemId);
    }

    @Test
    @DisplayName("Update booking. Successful update to CANCELLED")
    void shouldUpdateBookingToCancelledSuccessfully() {
        Long bookingId = 1L;
        Long bookerId = 1L;

        User booker = User.builder().id(bookerId).name("Booker").build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING)
                .booker(booker)
                .build();
        BookingUpdateDto bookingUpdateDto = BookingUpdateDto.builder()
                .id(bookingId)
                .status(Status.CANCELLED)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(BookingDto.builder().id(bookingId).status(Status.CANCELLED).build());

        BookingDto updatedBooking = bookingService.updateBooking(bookerId, bookingUpdateDto);

        assertNotNull(updatedBooking);
        assertEquals(Status.CANCELLED, updatedBooking.getStatus());
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Update booking. Successful update in WAITING")
    void shouldUpdateBookingTimeWhenStatusWaiting() {
        Long bookingId = 1L;
        Long bookerId = 1L;

        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(User.builder().id(bookerId).build())
                .status(Status.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingUpdateDto updateDto = BookingUpdateDto.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(BookingDto.builder()
                .id(bookingId)
                .status(Status.WAITING)
                .build());

        BookingDto updatedBooking = bookingService.updateBooking(bookerId, updateDto);

        assertNotNull(updatedBooking);
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Update booking. Booking is not found")
    void shouldThrowNoSuchBookingExceptionWhenBookingNotFound() {
        Long bookerId = 1L;
        Long bookingId = 2L;

        BookingUpdateDto bookingUpdateDto = BookingUpdateDto.builder()
                .id(bookingId)
                .status(Status.CANCELLED)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NoSuchBookingException exception = assertThrows(
                NoSuchBookingException.class,
                () -> bookingService.updateBooking(bookerId, bookingUpdateDto)
        );

        assertEquals("Incorrect booking id: " + bookingId, exception.getMessage());

        verify(bookingRepository).findById(bookingId);
    }

    @Test
    @DisplayName("Update booking. Update in another status")
    void shouldFailToUpdateBookingDatesInInvalidStatus() {
        Long bookingId = 1L;
        Long bookerId = 1L;

        User booker = User.builder().id(bookerId).name("Booker").build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.APPROVED)
                .booker(booker)
                .build();
        BookingUpdateDto bookingUpdateDto = BookingUpdateDto.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingAccessException exception = assertThrows(BookingAccessException.class,
                () -> bookingService.updateBooking(bookerId, bookingUpdateDto));

        assertEquals("Booking dates can only be updated in 'WAITING' status only", exception.getMessage());
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    @DisplayName("Update booking. Update from another status to CANCELLED")
    void shouldThrowExceptionWhenUpdatingStatusWithInvalidCurrentStatus() {
        Long bookingId = 1L;
        Long bookerId = 1L;

        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(User.builder().id(bookerId).build())
                .status(Status.REJECTED)
                .build();

        BookingUpdateDto updateDto = BookingUpdateDto.builder()
                .id(bookingId)
                .status(Status.CANCELLED)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingAccessException exception = assertThrows(BookingAccessException.class,
                () -> bookingService.updateBooking(bookerId, updateDto));

        assertEquals("Booking can be cancelled in statuses 'WAITING' or 'APPROVED' only", exception.getMessage());
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    @DisplayName("Get all bookings. State ALL")
    void shouldReturnAllBookingsByOwnerAndStateAll() {
        Long ownerId = 1L;
        String state = "ALL";

        User owner = User.builder()
                .id(ownerId)
                .name("Owner")
                .email("owner@example.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .status(Status.APPROVED)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByOwnerId(ownerId)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDto.builder().id(item.getId()).build())
                .booker(UserDto.builder().id(ownerId).build())
                .build());

        Collection<BookingDto> bookings = bookingService.findAllBookingsByOwnerAndState(ownerId, state);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findAllByOwnerId(ownerId);
    }

    @Test
    @DisplayName("Get all bookings. State CURRENT")
    void shouldReturnAllCurrentBookingsByOwnerAndStateCurrent() {
        Long ownerId = 1L;
        String state = "CURRENT";

        User owner = User.builder()
                .id(ownerId)
                .name("Owner")
                .email("owner@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .item(item)
                .status(Status.APPROVED)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllCurrentBookingByOwnerId(ownerId)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDto.builder().id(item.getId()).build())
                .booker(UserDto.builder().id(ownerId).build())
                .build());

        Collection<BookingDto> bookings = bookingService.findAllBookingsByOwnerAndState(ownerId, state);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findAllCurrentBookingByOwnerId(ownerId);
    }

    @Test
    @DisplayName("Get all bookings. State PAST")
    void shouldReturnAllPastBookingsByOwnerAndStatePast() {
        Long ownerId = 1L;
        String state = "PAST";

        User owner = User.builder()
                .id(ownerId)
                .name("Owner")
                .email("owner@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .status(Status.APPROVED)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllPastBookingByOwnerId(ownerId)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDto.builder().id(item.getId()).build())
                .booker(UserDto.builder().id(ownerId).build())
                .build());

        Collection<BookingDto> bookings = bookingService.findAllBookingsByOwnerAndState(ownerId, state);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findAllPastBookingByOwnerId(ownerId);
    }

    @Test
    @DisplayName("Get all bookings. State FUTURE")
    void shouldReturnAllFutureBookingsByOwnerAndStateFuture() {
        Long ownerId = 1L;
        String state = "FUTURE";

        User owner = User.builder()
                .id(ownerId)
                .name("Owner")
                .email("owner@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .status(Status.APPROVED)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllFutureBookingByOwnerId(ownerId)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDto.builder().id(item.getId()).build())
                .booker(UserDto.builder().id(ownerId).build())
                .build());

        Collection<BookingDto> bookings = bookingService.findAllBookingsByOwnerAndState(ownerId, state);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findAllFutureBookingByOwnerId(ownerId);
    }

    @Test
    @DisplayName("Get all bookings. State WAITING")
    void shouldReturnAllWaitingBookingsByOwnerAndStateWaiting() {
        Long ownerId = 1L;
        String state = "WAITING";

        User owner = User.builder()
                .id(ownerId)
                .name("Owner")
                .email("owner@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .status(Status.WAITING)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.WAITING)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDto.builder().id(item.getId()).build())
                .booker(UserDto.builder().id(ownerId).build())
                .build());

        Collection<BookingDto> bookings = bookingService.findAllBookingsByOwnerAndState(ownerId, state);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findAllByOwnerIdAndStatus(ownerId, Status.WAITING);
    }

    @Test
    @DisplayName("Get all bookings. State REJECTED")
    void shouldReturnAllRejectedBookingsByOwnerAndStateRejected() {
        Long ownerId = 1L;
        String state = "REJECTED";

        User owner = User.builder()
                .id(ownerId)
                .name("Owner")
                .email("owner@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .owner(owner)
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .status(Status.REJECTED)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.REJECTED)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(ItemDto.builder().id(item.getId()).build())
                .booker(UserDto.builder().id(ownerId).build())
                .build());

        Collection<BookingDto> bookings = bookingService.findAllBookingsByOwnerAndState(ownerId, state);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findAllByOwnerIdAndStatus(ownerId, Status.REJECTED);
    }

    @Test
    @DisplayName("Getting all bookings for user. State ALL")
    void shouldReturnAllBookingsForUserWhenStateIsAll() {
        Long userId = 1L;
        String state = "ALL";

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .booker(User.builder().id(userId).build())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(bookingRepository.findAllByBookerId(userId)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(BookingDto.builder().id(booking.getId()).build());

        Collection<BookingDto> result = bookingService.findAllBookingsByUserAndState(userId, state);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerId(userId);
    }

    @Test
    @DisplayName("Getting all bookings for user. State CURRENT")
    void shouldReturnCurrentBookingsForUserWhenStateIsCurrent() {
        Long userId = 1L;
        String state = "CURRENT";

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .booker(User.builder().id(userId).build())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(bookingRepository.findAllCurrentBookingsByBookerId(userId)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(BookingDto.builder().id(booking.getId()).build());

        Collection<BookingDto> result = bookingService.findAllBookingsByUserAndState(userId, state);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllCurrentBookingsByBookerId(userId);
    }

    @Test
    @DisplayName("Getting all bookings for user. State PAST")
    void shouldReturnPastBookingsForUserWhenStateIsPast() {
        Long userId = 1L;
        String state = "PAST";

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .booker(User.builder().id(userId).build())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(bookingRepository.findAllPastBookingByBookerId(userId)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(BookingDto.builder().id(booking.getId()).build());

        Collection<BookingDto> result = bookingService.findAllBookingsByUserAndState(userId, state);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllPastBookingByBookerId(userId);
    }

    @Test
    @DisplayName("Getting all bookings for user. State FUTURE")
    void shouldReturnFutureBookingsForUserWhenStateIsFuture() {
        Long userId = 1L;
        String state = "FUTURE";

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(User.builder().id(userId).build())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(bookingRepository.findAllFutureBookingByBookerId(userId)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(BookingDto.builder().id(booking.getId()).build());

        Collection<BookingDto> result = bookingService.findAllBookingsByUserAndState(userId, state);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllFutureBookingByBookerId(userId);
    }

    @Test
    @DisplayName("Getting all bookings for user. State WAITING")
    void shouldReturnWaitingBookingsForUserWhenStateIsWaiting() {
        Long userId = 1L;
        String state = "WAITING";

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING)
                .booker(User.builder().id(userId).build())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(bookingRepository.findAllByBookerIdAndStatus(userId, Status.WAITING)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(BookingDto.builder().id(booking.getId()).build());

        Collection<BookingDto> result = bookingService.findAllBookingsByUserAndState(userId, state);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdAndStatus(userId, Status.WAITING);
    }

    @Test
    @DisplayName("Getting all bookings for user. State REJECTED")
    void shouldReturnRejectedBookingsForUserWhenStateIsRejected() {
        Long userId = 1L;
        String state = "REJECTED";

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.REJECTED)
                .booker(User.builder().id(userId).build())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(bookingRepository.findAllByBookerIdAndStatus(userId, Status.REJECTED)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(BookingDto.builder().id(booking.getId()).build());

        Collection<BookingDto> result = bookingService.findAllBookingsByUserAndState(userId, state);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdAndStatus(userId, Status.REJECTED);
    }

    @Test
    @DisplayName("Find booking by ID. Successfully found")
    void shouldFindBookingByIdSuccessfully() {
        Long bookingId = 1L;
        Long userId = 1L;

        User booker = User.builder()
                .id(userId)
                .name("Booker")
                .email("example@yandex.ru")
                .build();
        Item item = Item.builder()
                .id(2L).name("Item Name").owner(booker).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(booker)
                .item(item)
                .status(Status.APPROVED)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(BookingDto.builder().id(bookingId).build());

        BookingDto foundBooking = bookingService.findBooking(bookingId, userId);

        assertNotNull(foundBooking);
        assertEquals(bookingId, foundBooking.getId());
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    @DisplayName("Find booking by ID. User is not the booker")
    void shouldThrowExceptionWhenUserNotBooker() {
        Long bookingId = 1L;
        Long bookerId = 2L;

        Booking booking = Booking.builder()
                .id(bookingId)
                .booker(User.builder().id(1L).build())
                .status(Status.WAITING)
                .build();

        BookingUpdateDto updateDto = BookingUpdateDto.builder()
                .id(bookingId)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingAccessException exception = assertThrows(BookingAccessException.class,
                () -> bookingService.updateBooking(bookerId, updateDto));

        assertEquals("Booker can update booking only", exception.getMessage());
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    @DisplayName("Approve booking. Successful approve")
    void shouldApproveBookingSuccessfully() {
        Long bookingId = 1L;
        Long userId = 2L;

        Item item = Item.builder().id(1L).owner(User.builder().id(userId).build()).available(true).build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .status(Status.WAITING)
                .build();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(BookingDto.builder().id(bookingId).status(Status.APPROVED).build());

        BookingDto approvedBooking = bookingService.approveBooking(bookingId, userId, true);

        assertNotNull(approvedBooking);
        assertEquals(Status.APPROVED, approvedBooking.getStatus());
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Approve booking. Approved by not owner")
    void shouldFailToApproveBookingByNonOwner() {
        Long bookingId = 1L;
        Long userId = 3L;

        Item item = Item.builder()
                .id(1L)
                .owner(User.builder().id(2L).build())
                .available(true)
                .build();
        Booking booking = Booking.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .status(Status.WAITING)
                .build();

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        CommentIncorrectTimeException exception = assertThrows(CommentIncorrectTimeException.class,
                () -> bookingService.approveBooking(bookingId, userId, true));

        assertEquals("Booking can be approved by owner only", exception.getMessage());
        verify(bookingRepository).findById(bookingId);
        verify(itemRepository).findById(item.getId());
    }
}