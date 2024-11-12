package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class UserUpdateDto {
    @Positive(message = "ID must be positive number")
    private Long id;
    private String name;
    @Email(message = "Incorrect email format")
    private String email;
}