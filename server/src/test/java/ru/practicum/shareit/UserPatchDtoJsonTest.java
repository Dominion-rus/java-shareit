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

        assertThat(json.parse(jsonContent)).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void deserializeUserPatchDto_WithMissingEmail() throws Exception {
        String jsonContent = "{\n" +
                "    \"name\": \"Updated Name\"\n" +
                "}";

        UserPatchDto expectedDto = new UserPatchDto("Updated Name", null);

        assertThat(json.parse(jsonContent).getObject()).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void deserializeUserPatchDto_WithMissingName() throws Exception {
        String jsonContent = "{\n" +
                "    \"email\": \"updated@example.com\"\n" +
                "}";

        UserPatchDto expectedDto = new UserPatchDto(null, "updated@example.com");

        assertThat(json.parse(jsonContent).getObject()).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void deserializeUserPatchDto_WithEmptyFields() throws Exception {
        String jsonContent = "{\n" +
                "    \"name\": \"\",\n" +
                "    \"email\": \"\"\n" +
                "}";

        UserPatchDto expectedDto = new UserPatchDto("", "");

        assertThat(json.parse(jsonContent).getObject()).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void deserializeUserPatchDto_WithNullFields() throws Exception {
        String jsonContent = "{\n" +
                "    \"name\": null,\n" +
                "    \"email\": null\n" +
                "}";

        UserPatchDto expectedDto = new UserPatchDto(null, null);

        assertThat(json.parse(jsonContent).getObject()).usingRecursiveComparison().isEqualTo(expectedDto);
    }
}
