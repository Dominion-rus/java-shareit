package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void serializeItemRequestDto_WithNullValues() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(null, null, null);

        String jsonContent = "{\n" +
                "    \"id\": null,\n" +
                "    \"description\": null,\n" +
                "    \"created\": null\n" +
                "}";

        assertThat(json.write(requestDto)).isEqualToJson(jsonContent);
    }

    @Test
    void deserializeItemRequestDto_WithNullValues() throws Exception {
        String jsonContent = "{\n" +
                "    \"id\": null,\n" +
                "    \"description\": null,\n" +
                "    \"created\": null\n" +
                "}";

        ItemRequestDto expectedDto = new ItemRequestDto(null, null, null);

        assertThat(json.parse(jsonContent)).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void deserializeItemRequestDto_WithPartialFields() throws Exception {
        String jsonContent = "{\n" +
                "    \"description\": \"Need a laptop\"\n" +
                "}";

        ItemRequestDto expectedDto = new ItemRequestDto(null, "Need a laptop", null);

        assertThat(json.parse(jsonContent).getObject())
                .usingRecursiveComparison()
                .isEqualTo(expectedDto);
    }


    @Test
    void deserializeItemRequestDto_ShouldThrowException_WhenInvalidJson() {
        String invalidJsonContent = "{\n" +
                "    \"id\": \"wrong-value\"\n" +
                "}";

        assertThatThrownBy(() -> json.parse(invalidJsonContent))
                .isInstanceOf(Exception.class);
    }
}
