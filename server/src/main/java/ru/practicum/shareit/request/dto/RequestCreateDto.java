package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestCreateDto {
    private String description;
    Long requestorId;
}