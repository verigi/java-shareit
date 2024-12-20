package ru.practicum.shareit.item.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemCreateDto {
    @NotBlank(message = "Name must not be blank or null")
    @NotEmpty
    private String name;
    @NotBlank(message = "Name must not be blank or null")
    private String description;
    @NotNull(message = "Availability must not be null")
    private Boolean available;
    @Positive(message = "Owner ID must be positive number")
    private Long ownerId;
    @Nullable
    private Long requestId;
}