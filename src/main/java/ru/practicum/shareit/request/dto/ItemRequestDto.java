package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class ItemRequestDto {
    private Long id;
    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be blank")
    private String description;
    @NotNull(message = "Requestor must not be null")
    private User requestor;
    private LocalDateTime created;
}
