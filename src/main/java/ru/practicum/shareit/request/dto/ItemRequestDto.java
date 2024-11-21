package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}