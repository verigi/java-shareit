package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemExpandedDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemJsonTest {

    @Autowired
    private JacksonTester<ItemExpandedDto> jsonTester;

    @Test
    @DisplayName("Serialization of ItemExpandedDto")
    void shouldSerializeItemExpandedDtoToJson() throws Exception {
        LocalDateTime thisDay = LocalDateTime.of(2024, 12, 1, 10, 0);
        LocalDateTime nextDay = LocalDateTime.of(2024, 12, 2, 15, 0);

        List<CommentDto> comments = List.of(
                CommentDto.builder().id(1L).text("Great item!").build(),
                CommentDto.builder().id(2L).text("Super!").build()
        );

        ItemExpandedDto item = ItemExpandedDto.builder()
                .id(1L)
                .name("Laptop for programming")
                .description("A very good laptop")
                .available(true)
                .lastBooking(thisDay)
                .nextBooking(nextDay)
                .comments(comments)
                .ownerId(1L)
                .requestId(2L)
                .build();

        JsonContent<ItemExpandedDto> result = jsonTester.write(item);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Laptop for programming");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("A very good laptop");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo("2024-12-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo("2024-12-02T15:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Great item!");
        assertThat(result).extractingJsonPathNumberValue("$.comments[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.comments[1].text").isEqualTo("Super!");
    }

    @Test
    @DisplayName("Deserialization of ItemExpandedDto")
    void shouldDeserializeJsonToItemExpandedDto() throws Exception {
        String json = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Laptop\",\n" +
                "    \"description\": \"Very good laptop\",\n" +
                "    \"available\": true,\n" +
                "    \"lastBooking\": \"2024-12-01T10:00:00\",\n" +
                "    \"nextBooking\": \"2024-12-02T15:00:00\",\n" +
                "    \"comments\": [\n" +
                "        {\"id\": 1, \"text\": \"Great item!\"},\n" +
                "        {\"id\": 2, \"text\": \"Superr!\"}\n" +
                "    ],\n" +
                "    \"ownerId\": 1,\n" +
                "    \"requestId\": 2\n" +
                "}";

        ItemExpandedDto item = jsonTester.parseObject(json);

        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Laptop");
        assertThat(item.getDescription()).isEqualTo("Very good laptop");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getLastBooking()).isEqualTo(LocalDateTime.of(2024, 12, 1, 10, 0));
        assertThat(item.getNextBooking()).isEqualTo(LocalDateTime.of(2024, 12, 2, 15, 0));
        assertThat(item.getOwnerId()).isEqualTo(1L);
        assertThat(item.getRequestId()).isEqualTo(2L);
        assertThat(item.getComments()).hasSize(2);
        assertThat(item.getComments().get(0).getText()).isEqualTo("Great item!");
        assertThat(item.getComments().get(1).getText()).isEqualTo("Superr!");
    }
}