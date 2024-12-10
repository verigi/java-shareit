package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class CommentUpdateDto {
    private Long id;
    private String text;
}