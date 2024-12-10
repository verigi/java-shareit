package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserJsonTest {

    @Autowired
    private JacksonTester<UserDto> userJsonTester;

    @Test
    @DisplayName("Serialization of UserDto")
    void shouldSerializeUserDtoToJson() throws Exception {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@yandex.ru")
                .build();

        JsonContent<UserDto> result = userJsonTester.write(user);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@yandex.ru");
    }

    @Test
    @DisplayName("Deserialization of UserDto")
    void shouldDeserializeJsonToUserDto() throws Exception {
        String json = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"user\",\n" +
                "    \"email\": \"user@yandex.ru\"\n" +
                "}";

        ObjectContent<UserDto> user = userJsonTester.parse(json);

        assertThat(user).isNotNull();
        assertThat(user.getObject().getId()).isEqualTo(1L);
        assertThat(user.getObject().getName()).isEqualTo("user");
        assertThat(user.getObject().getEmail()).isEqualTo("user@yandex.ru");
    }
}