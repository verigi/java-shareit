package ru.practicum.shareit.item.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentUpdateDto;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public CommentDto toDto(Comment comment) {
        return comment == null ? null : CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment toComment(CommentCreateDto commentDto, Item item, User author) {
        return commentDto == null ? null : Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    public Comment updateCommentFromDto(CommentUpdateDto commentDto, Comment comment) {
        if (commentDto.getText() != null) comment.setText(commentDto.getText());

        return comment;
    }
}