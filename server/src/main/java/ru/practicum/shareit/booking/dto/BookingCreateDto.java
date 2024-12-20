package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateDto {
    @NotNull(message = "Start of booking must not be null")
    @Future(message = "Start of booking must be in future")
    private LocalDateTime start;
    @NotNull(message = "End of booking must not be null")
    @Future(message = "End of booking must be in future")
    private LocalDateTime end;
    @NotNull(message = "Booking item ID must not be null")
    private Long itemId;
    private Long bookerId;
}