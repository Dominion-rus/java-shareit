package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void serializeItemRequestDto() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Need a laptop",
                LocalDateTime.of(2025, 3, 1, 12, 0));

        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"description\": \"Need a laptop\",\n" +
                "    \"created\": \"2025-03-01T12:00:00\"\n" +
                "}";

        assertThat(json.write(requestDto)).isEqualToJson(jsonContent);
    }

    @Test
    void deserializeItemRequestDto() throws Exception {
        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"description\": \"Need a laptop\",\n" +
                "    \"created\": \"2025-03-01T12:00:00\"\n" +
                "}";

        assertThat(json.parse(jsonContent).getObject())
                .usingRecursiveComparison()
                .isEqualTo(new ItemRequestDto(1L, "Need a laptop",
                LocalDateTime.of(2025, 3, 1, 12, 0)));
    }
}

