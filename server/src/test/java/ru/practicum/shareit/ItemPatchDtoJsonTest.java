package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JsonTest
class ItemPatchDtoJsonTest {

    @Autowired
    private JacksonTester<ItemPatchDto> json;

    @Test
    void serializeItemPatchDto() throws Exception {
        ItemPatchDto itemPatchDto = new ItemPatchDto("Updated Laptop",
                "Updated description", false, 10L);

        String jsonContent = "{\n" +
                "    \"name\": \"Updated Laptop\",\n" +
                "    \"description\": \"Updated description\",\n" +
                "    \"available\": false,\n" +
                "    \"requestId\": 10\n" +
                "}";

        assertThat(json.write(itemPatchDto)).isEqualToJson(jsonContent);
    }

    @Test
    void deserializeItemPatchDto() throws Exception {
        String jsonContent = "{\n" +
                "    \"name\": \"Updated Laptop\",\n" +
                "    \"description\": \"Updated description\",\n" +
                "    \"available\": false,\n" +
                "    \"requestId\": 10\n" +
                "}";

        ItemPatchDto expectedDto = new ItemPatchDto("Updated Laptop", "Updated description",
                false, 10L);

        assertThat(json.parse(jsonContent)).isEqualTo(expectedDto);
    }

    @Test
    void serializeItemPatchDto_WithNullValues() throws Exception {
        ItemPatchDto itemPatchDto = new ItemPatchDto(null, null, null, null);

        String jsonContent = "{\n" +
                "    \"name\": null,\n" +
                "    \"description\": null,\n" +
                "    \"available\": null,\n" +
                "    \"requestId\": null\n" +
                "}";

        assertThat(json.write(itemPatchDto)).isEqualToJson(jsonContent);
    }

    @Test
    void deserializeItemPatchDto_WithNullValues() throws Exception {
        String jsonContent = "{\n" +
                "    \"name\": null,\n" +
                "    \"description\": null,\n" +
                "    \"available\": null,\n" +
                "    \"requestId\": null\n" +
                "}";

        ItemPatchDto expectedDto = new ItemPatchDto(null, null, null, null);

        assertThat(json.parse(jsonContent)).isEqualTo(expectedDto);
    }

    @Test
    void deserializeItemPatchDto_WithPartialFields() throws Exception {
        String jsonContent = "{\n" +
                "    \"name\": \"Updated Laptop\"\n" +
                "}";

        ItemPatchDto expectedDto = new ItemPatchDto("Updated Laptop", null, null, null);

        assertThat(json.parse(jsonContent)).isEqualTo(expectedDto);
    }

    @Test
    void deserializeItemPatchDto_ShouldThrowException_WhenInvalidJson() {
        String invalidJsonContent = "{\n" +
                "    \"name\": \"Updated Laptop\",\n" +
                "    \"available\": \"wrong-value\"\n" + // Некорректный boolean
                "}";

        assertThatThrownBy(() -> json.parse(invalidJsonContent))
                .isInstanceOf(Exception.class);
    }
}
