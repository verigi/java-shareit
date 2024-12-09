package ru.practicum.shareit.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentJsonTest {

    @Autowired
    private JacksonTester<CommentDto> jsonTester;

    @Test
    @DisplayName("Serialization of CommentDto")
    void shouldSerializeCommentDtoToJson() throws Exception {
        LocalDateTime createdDate = LocalDateTime.of(2024, 12, 1, 10, 0);

        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("Test comment!")
                .itemId(2L)
                .authorName("User Userov")
                .created(createdDate)
                .build();

        JsonContent<CommentDto> result = jsonTester.write(comment);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Test comment!");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("User Userov");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-12-01T10:00:00");
    }

    @Test
    @DisplayName("Deserialization of CommentDto")
    void shouldDeserializeJsonToCommentDto() throws Exception {
        String json = "{\n" +
                "    \"id\": 1,\n" +
                "    \"text\": \"Test comment!\",\n" +
                "    \"itemId\": 2,\n" +
                "    \"authorName\": \"User Userov\",\n" +
                "    \"created\": \"2024-12-01T10:00:00\"\n" +
                "}";

        CommentDto comment = jsonTester.parseObject(json);

        assertThat(comment).isNotNull();
        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getText()).isEqualTo("Test comment!");
        assertThat(comment.getItemId()).isEqualTo(2L);
        assertThat(comment.getAuthorName()).isEqualTo("User Userov");
        assertThat(comment.getCreated()).isEqualTo(LocalDateTime.of(2024, 12, 1, 10, 0));
    }
}