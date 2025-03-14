package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serializeBookingDto() throws Exception {
        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.of(2025, 3, 1, 12, 0),
                LocalDateTime.of(2025, 3, 2, 12, 0));

        String jsonContent = "{"
                + "\"itemId\": 1,"
                + "\"start\": \"2025-03-01T12:00:00\","
                + "\"end\": \"2025-03-02T12:00:00\""
                + "}";

        assertThat(json.write(bookingDto)).isEqualToJson(jsonContent);
    }

    @Test
    void deserializeBookingDto() throws Exception {
        String jsonContent = "{"
                + "\"itemId\": 1,"
                + "\"start\": \"2025-03-01T12:00:00\","
                + "\"end\": \"2025-03-02T12:00:00\""
                + "}";

        BookingDto expectedDto = new BookingDto(1L,
                LocalDateTime.of(2025, 3, 1, 12, 0),
                LocalDateTime.of(2025, 3, 2, 12, 0));

        assertThat(json.parse(jsonContent)).isEqualTo(expectedDto);
    }

    @Test
    void serializeBookingDto_WithNullValues() throws Exception {
        BookingDto bookingDto = new BookingDto(null, null, null);

        String jsonContent = "{"
                + "\"itemId\": null,"
                + "\"start\": null,"
                + "\"end\": null"
                + "}";

        assertThat(json.write(bookingDto)).isEqualToJson(jsonContent);
    }

    @Test
    void deserializeBookingDto_WithNullValues() throws Exception {
        String jsonContent = "{"
                + "\"itemId\": null,"
                + "\"start\": null,"
                + "\"end\": null"
                + "}";

        BookingDto expectedDto = new BookingDto(null, null, null);

        assertThat(json.parse(jsonContent)).isEqualTo(expectedDto);
    }

    @Test
    void deserializeBookingDto_ShouldThrowException_WhenInvalidDateFormat() {
        String invalidJsonContent = "{"
                + "\"itemId\": 1,"
                + "\"start\": \"invalid-date\","
                + "\"end\": \"2025-03-02T12:00:00\""
                + "}";

        assertThatThrownBy(() -> json.parse(invalidJsonContent))
                .isInstanceOf(Exception.class);
    }
}
