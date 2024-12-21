package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestCreateDto {
    @NotNull(message = "Description must not be null")
    private String description;
    Long requestorId;
}