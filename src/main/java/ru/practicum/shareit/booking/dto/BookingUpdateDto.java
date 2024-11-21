package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class BookingUpdateDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private Status status;
}