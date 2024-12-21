package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestExpandedDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class RequestJsonTest {

    @Autowired
    private JacksonTester<RequestExpandedDto> jsonTester;

    @Test
    @DisplayName("Serialization of RequestExpandedDto")
    void shouldSerializeRequestExpandedDtoToJson() throws Exception {
        LocalDateTime created = LocalDateTime.of(2024, 12, 2, 10, 0);

        UserDto requestor = UserDto.builder()
                .id(1L)
                .name("User Name")
                .email("user@yandex.ru")
                .build();

        ItemDto item1 = ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .description("First item")
                .available(true)
                .build();

        ItemDto item2 = ItemDto.builder()
                .id(2L)
                .name("Item 2")
                .description("Second item")
                .available(false)
                .build();

        RequestExpandedDto requestExpandedDto = RequestExpandedDto.builder()
                .id(1L)
                .description("Request Description")
                .requestor(requestor)
                .created(created)
                .items(List.of(item1, item2))
                .build();

        JsonContent<RequestExpandedDto> result = jsonTester.write(requestExpandedDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Request Description");
        assertThat(result).extractingJsonPathStringValue("$.requestor.name").isEqualTo("User Name");
        assertThat(result).extractingJsonPathStringValue("$.requestor.email").isEqualTo("user@yandex.ru");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2024-12-02T10:00:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(2);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Item 1");
    }

    @Test
    @DisplayName("Deserialization of RequestExpandedDto")
    void shouldDeserializeJsonToRequestExpandedDto() throws Exception {
        String json = "{\n" +
                "    \"id\": 1,\n" +
                "    \"description\": \"Request Test Description\",\n" +
                "    \"requestor\": {\n" +
                "        \"id\": 1,\n" +
                "        \"name\": \"User Name\",\n" +
                "        \"email\": \"user@yandex.ru\"\n" +
                "    },\n" +
                "    \"created\": \"2024-12-02T10:00:00\",\n" +
                "    \"items\": [\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"name\": \"Item 1\",\n" +
                "            \"description\": \"First item\",\n" +
                "            \"available\": true\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 2,\n" +
                "            \"name\": \"Item 2\",\n" +
                "            \"description\": \"Second item\",\n" +
                "            \"available\": false\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        RequestExpandedDto requestExpandedDto = jsonTester.parseObject(json);

        assertThat(requestExpandedDto).isNotNull();
        assertThat(requestExpandedDto.getId()).isEqualTo(1L);
        assertThat(requestExpandedDto.getDescription()).isEqualTo("Request Test Description");
        assertThat(requestExpandedDto.getRequestor().getName()).isEqualTo("User Name");
        assertThat(requestExpandedDto.getRequestor().getEmail()).isEqualTo("user@yandex.ru");
        assertThat(requestExpandedDto.getCreated()).isEqualTo(LocalDateTime.of(2024, 12, 2, 10, 0));
        assertThat(requestExpandedDto.getItems()).hasSize(2);
        assertThat(requestExpandedDto.getItems().get(0).getName()).isEqualTo("Item 1");
    }
}