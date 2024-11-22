package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentCreateDto {
    @NotNull(message = "Text must not be null")
    @NotBlank(message = "Text must not be blank")
    String text;

    @NotNull(message = "Item must not be null")
    Long itemId;

    @NotNull(message = "Author must not be null")
    Long authorId;
}