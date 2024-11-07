package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
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
public class BookingDto {
    private Long id;
    @NotNull(message = "Start of booking must not be null")
    @FutureOrPresent(message = "Start of booking must be in future")
    private LocalDateTime bookingStart;
    @NotNull(message = "End of booking must not be null")
    @FutureOrPresent(message = "End of booking must be in future")
    private LocalDateTime bookingEnd;
    @NotNull(message = "Booking item must not be null")
    private Item item;
    @NotNull(message = "Booker must not be null")
    private User booker;
    @NotNull(message = "Booking status required")
    private BookingStatus bookingStatus;
}
