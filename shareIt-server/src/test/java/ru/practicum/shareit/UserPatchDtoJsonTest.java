package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserPatchDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserPatchDtoJsonTest {

    @Autowired
    private JacksonTester<UserPatchDto> json;

    @Test
    void serializeUserPatchDto() throws Exception {
        UserPatchDto patchDto = new UserPatchDto("Updated Name", "updated@example.com");


        String jsonContent = "{\n" +
                "    \"name\": \"Updated Name\",\n" +
                "    \"email\": \"updated@example.com\"\n" +
                "}";

        assertThat(json.write(patchDto)).isEqualToJson(jsonContent);

    }

    @Test
    void deserializeUserPatchDto() throws Exception {

        String jsonContent = "{\n" +
                "    \"name\": \"Updated Name\",\n" +
                "    \"email\": \"updated@example.com\"\n" +
                "}";

        UserPatchDto expectedDto = new UserPatchDto("Updated Name", "updated@example.com");

        assertThat(json.parse(jsonContent)).isEqualTo(expectedDto);
    }
}

