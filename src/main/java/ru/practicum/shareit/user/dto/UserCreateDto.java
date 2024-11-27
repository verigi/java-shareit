package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDto {
    @NotBlank(message = "Name must not be blank or null")
    private String name;
    @NotBlank(message = "Email must not be blank or null")
    @Email(message = "Incorrect email format")
    private String email;
}