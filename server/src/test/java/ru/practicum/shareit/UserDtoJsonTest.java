package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void serializeUserDto() throws Exception {
        UserDto userDto = new UserDto(1L, "John Doe", "john@example.com");

        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"email\": \"john@example.com\"\n" +
                "}";

        assertThat(json.write(userDto)).isEqualToJson(jsonContent);
    }

    @Test
    void deserializeUserDto() throws Exception {
        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"email\": \"john@example.com\"\n" +
                "}";

        UserDto expectedDto = new UserDto(1L, "John Doe", "john@example.com");

        assertThat(json.parse(jsonContent)).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void deserializeUserDto_WithMissingId() throws Exception {
        String jsonContent = "{\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"email\": \"john@example.com\"\n" +
                "}";

        UserDto expectedDto = new UserDto(null, "John Doe", "john@example.com");

        assertThat(json.parse(jsonContent).getObject()).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void deserializeUserDto_WithMissingEmail() throws Exception {
        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"John Doe\"\n" +
                "}";

        UserDto expectedDto = new UserDto(1L, "John Doe", null);

        assertThat(json.parse(jsonContent).getObject()).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void deserializeUserDto_WithEmptyFields() throws Exception {
        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"\",\n" +
                "    \"email\": \"\"\n" +
                "}";

        UserDto expectedDto = new UserDto(1L, "", "");

        assertThat(json.parse(jsonContent).getObject()).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void deserializeUserDto_WithNullFields() throws Exception {
        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": null,\n" +
                "    \"email\": null\n" +
                "}";

        UserDto expectedDto = new UserDto(1L, null, null);

        assertThat(json.parse(jsonContent).getObject()).usingRecursiveComparison().isEqualTo(expectedDto);
    }
}
