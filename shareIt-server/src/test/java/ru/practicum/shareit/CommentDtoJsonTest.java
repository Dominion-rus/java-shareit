package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

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
}
