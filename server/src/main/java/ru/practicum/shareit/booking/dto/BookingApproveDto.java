package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class BookingApproveDto {
    private java.lang.Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private User booker;
    private Status status;
}