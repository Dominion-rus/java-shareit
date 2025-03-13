package ru.practicum.shareitgateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitgateway.config.AppConfig;
import ru.practicum.shareitgateway.item.ItemController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@Import(AppConfig.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private AppConfig appConfig;


    @Test
    void shouldReturnBadRequestForInvalidItem() throws Exception {
        String invalidItemJson = "{\"name\": \"\", \"description\": \"\", \"available\": null}";

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType("application/json")
                        .content(invalidItemJson))
                .andExpect(status().isBadRequest());
    }

}
