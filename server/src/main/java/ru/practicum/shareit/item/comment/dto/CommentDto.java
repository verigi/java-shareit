package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class CommentDto {
    @Positive(message = "ID must be positive number")
    private Long id;
    @NotNull(message = "Text must not be null")
    @NotBlank(message = "Text must not be blank")
    private String text;
    @NotNull(message = "Item must not be null")
    @NotBlank(message = "Item must not be blank")
    private Long itemId;
    @NotNull(message = "Author must not be null")
    @NotBlank(message = "Author must not be blank")
    private String authorName;
    @NotNull(message = "Create date must not be null")
    private LocalDateTime created;
}