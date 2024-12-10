package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class BookingJsonTest {

    @Autowired
    private JacksonTester<BookingCreateDto> jsonTester;

    @Test
    @DisplayName("Serialization of BookingCreateDto")
    void shouldSerializeBookingCreateDtoToJson() throws Exception {
        LocalDateTime start = LocalDateTime.of(2024, 12, 2, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 3, 15, 0);

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .start(start)
                .end(end)
                .itemId(1L)
                .bookerId(2L)
                .build();

        JsonContent<BookingCreateDto> result = jsonTester.write(bookingCreateDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-12-02T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-12-03T15:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
    }

    @Test
    @DisplayName("Deserialization of BookingCreateDto")
    void shouldDeserializeJsonToBookingCreateDto() throws Exception {
        String json = "{\n" +
                "    \"start\": \"2024-12-02T10:00:00\",\n" +
                "    \"end\": \"2024-12-03T15:00:00\",\n" +
                "    \"itemId\": 1,\n" +
                "    \"bookerId\": 2\n" +
                "}";

        BookingCreateDto bookingCreateDto = jsonTester.parseObject(json);

        assertThat(bookingCreateDto).isNotNull();
        assertThat(bookingCreateDto.getStart()).isEqualTo(LocalDateTime.of(2024, 12, 2, 10, 0));
        assertThat(bookingCreateDto.getEnd()).isEqualTo(LocalDateTime.of(2024, 12, 3, 15, 0));
        assertThat(bookingCreateDto.getItemId()).isEqualTo(1L);
        assertThat(bookingCreateDto.getBookerId()).isEqualTo(2L);
    }
}