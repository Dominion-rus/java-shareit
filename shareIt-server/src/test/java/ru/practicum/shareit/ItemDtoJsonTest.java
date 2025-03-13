package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void serializeItemDto() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Laptop", "Powerful laptop",
                true, null, null, null, null);

        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Laptop\",\n" +
                "    \"description\": \"Powerful laptop\",\n" +
                "    \"available\": true\n" +
                "}";

        assertThat(json.write(itemDto)).isEqualToJson(jsonContent);
    }

    @Test
    void deserializeItemDto() throws Exception {

        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Laptop\",\n" +
                "    \"description\": \"Powerful laptop\",\n" +
                "    \"available\": true\n" +
                "}";

        assertThat(json.parse(jsonContent).getObject())
                .usingRecursiveComparison()
                .isEqualTo(new ItemDto(1L, "Laptop", "Powerful laptop", true,
                        null, null, null, null));

    }
}

