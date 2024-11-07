package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class Booking {
    private Long id;
    private LocalDateTime bookingStart;
    private LocalDateTime bookingEnd;
    private Item item;
    private User booker;
    private BookingStatus bookingStatus;
}
