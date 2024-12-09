package ru.practicum.shareit.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentUpdateDto;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {

    private final CommentMapper commentMapper = new CommentMapper();

    @Test
    @DisplayName("Mapping Comment to CommentDto")
    void shouldMapCommentToCommentDto() {
        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .build();

        User author = User.builder()
                .id(2L)
                .name("Author Name")
                .build();

        Comment comment = Comment.builder()
                .id(3L)
                .text("This is a test comment")
                .item(item)
                .author(author)
                .created(LocalDateTime.of(2024, 12, 1, 10, 0))
                .build();

        CommentDto commentDto = commentMapper.toDto(comment);

        assertNotNull(commentDto);
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getItem().getId(), commentDto.getItemId());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    @DisplayName("Mapping CommentCreateDto to Comment")
    void shouldMapCommentCreateDtoToComment() {
        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("This is a test comment")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .build();

        User author = User.builder()
                .id(2L)
                .name("Author Name")
                .build();

        Comment comment = commentMapper.toComment(commentCreateDto, item, author);

        assertNotNull(comment);
        assertEquals(commentCreateDto.getText(), comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertNotNull(comment.getCreated());
    }

    @Test
    @DisplayName("Updating Comment from CommentUpdateDto")
    void shouldUpdateCommentFromCommentUpdateDto() {
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .text("Updated text")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("Original text")
                .build();

        Comment updatedComment = commentMapper.updateCommentFromDto(commentUpdateDto, comment);

        assertNotNull(updatedComment);
        assertEquals(commentUpdateDto.getText(), updatedComment.getText());
    }

    @Test
    @DisplayName("Mapping null Comment to CommentDto")
    void shouldReturnNullWhenMappingNullCommentToCommentDto() {
        CommentDto commentDto = commentMapper.toDto(null);
        assertNull(commentDto);
    }

    @Test
    @DisplayName("Mapping null CommentCreateDto to Comment")
    void shouldReturnNullWhenMappingNullCommentCreateDtoToComment() {
        Comment comment = commentMapper.toComment(null, null, null);
        assertNull(comment);
    }
}