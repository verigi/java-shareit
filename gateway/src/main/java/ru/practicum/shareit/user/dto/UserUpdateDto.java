package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class UserUpdateDto {
    private Long id;
    private String name;
    private String email;
}