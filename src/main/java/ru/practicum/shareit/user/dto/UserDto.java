package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class UserDto {
    @Positive(message = "ID must be positive number")
    private Long id;
    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotNull(message = "Email must not be null")
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Incorrect email format")
    private String email;
}
