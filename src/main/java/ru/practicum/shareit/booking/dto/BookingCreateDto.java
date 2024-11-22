package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingCreateDto {
    @NotNull(message = "Start of booking must not be null")
    @FutureOrPresent(message = "Start of booking must be in future")
    private LocalDateTime start;
    @NotNull(message = "End of booking must not be null")
    @Future(message = "End of booking must be in future")
    private LocalDateTime end;
    @NotNull(message = "Booking item ID must not be null")
    private Long itemId;
    private Long bookerId;
}