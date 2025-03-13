package ru.practicum.shareitgateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitgateway.config.AppConfig;
import ru.practicum.shareitgateway.user.UserController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(AppConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private AppConfig appConfig;

    @Test
    void shouldReturnBadRequestForInvalidUser() throws Exception {
        String invalidUserJson = "{\"name\": \"\", \"email\": \"invalidEmail\"}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());
    }
}
