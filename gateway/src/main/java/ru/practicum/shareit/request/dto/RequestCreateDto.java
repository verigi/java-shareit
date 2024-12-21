package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestCreateDto {
    @NotBlank(message = "Description must not be blank")
    private String description;
    Long requestorId;
}