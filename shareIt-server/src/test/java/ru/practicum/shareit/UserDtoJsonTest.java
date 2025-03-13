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

        assertThat(json.parse(jsonContent)).isEqualTo(expectedDto);
    }
}

