package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class ItemUpdateDto {
    @Positive(message = "ID must be positive number")
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @Positive(message = "Owner ID must be positive number")
    private Long ownerId;
    @Positive(message = "Request ID must be positive number")
    private Long requestId;
}
