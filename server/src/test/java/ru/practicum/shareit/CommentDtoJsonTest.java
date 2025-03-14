package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void serializeCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto(1L, "Great item!", "John Doe",
                LocalDateTime.of(2025, 3, 1, 12, 0));

        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"text\": \"Great item!\",\n" +
                "    \"authorName\": \"John Doe\",\n" +
                "    \"created\": \"2025-03-01T12:00:00\"\n" +
                "}";

        assertThat(json.write(commentDto)).isEqualToJson(jsonContent);
    }

    @Test
    void deserializeCommentDto() throws Exception {
        String jsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"text\": \"Great item!\",\n" +
                "    \"authorName\": \"John Doe\",\n" +
                "    \"created\": \"2025-03-01T12:00:00\"\n" +
                "}";

        CommentDto expectedDto = new CommentDto(1L, "Great item!", "John Doe",
                LocalDateTime.of(2025, 3, 1, 12, 0));

        assertThat(json.parse(jsonContent)).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void serializeCommentDto_WithNullValues() throws Exception {
        CommentDto commentDto = new CommentDto(null, null, null, null);

        String jsonContent = "{\n" +
                "    \"id\": null,\n" +
                "    \"text\": null,\n" +
                "    \"authorName\": null,\n" +
                "    \"created\": null\n" +
                "}";

        assertThat(json.write(commentDto)).isEqualToJson(jsonContent);
    }

    @Test
    void deserializeCommentDto_WithNullValues() throws Exception {
        String jsonContent = "{\n" +
                "    \"id\": null,\n" +
                "    \"text\": null,\n" +
                "    \"authorName\": null,\n" +
                "    \"created\": null\n" +
                "}";

        CommentDto expectedDto = new CommentDto(null, null, null, null);

        assertThat(json.parse(jsonContent)).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void deserializeCommentDto_ShouldThrowException_WhenInvalidDateFormat() {
        String invalidJsonContent = "{\n" +
                "    \"id\": 1,\n" +
                "    \"text\": \"Nice!\",\n" +
                "    \"authorName\": \"John Doe\",\n" +
                "    \"created\": \"invalid-date\"\n" +
                "}";

        assertThatThrownBy(() -> json.parse(invalidJsonContent))
                .isInstanceOf(Exception.class);
    }
}
