package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class ItemDto {
    @Positive(message = "ID must be positive number")
    private Long id;
    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    private String description;
    @NotNull(message = "")
    private Boolean available;
    @Positive(message = "Owner ID must be positive number")
    private Long ownerId;
    @Positive(message = "Request ID must be positive number")
    private Long requestId;
}