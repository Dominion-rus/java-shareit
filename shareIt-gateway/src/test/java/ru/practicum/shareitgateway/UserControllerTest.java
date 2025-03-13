package ru.practicum.shareitgateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitgateway.config.AppConfig;
import ru.practicum.shareitgateway.user.UserController;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldReturnBadRequestForInvalidUser() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        String invalidUserJson = "{\"name\": \"\", \"email\": \"invalidEmail\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        String validUserJson = "{\"name\": \"John Doe\", \"email\": \"john@example.com\"}";

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/users");
        when(restTemplate.postForEntity(anyString(), any(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>("{}", HttpStatus.CREATED));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserJson))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        String updateUserJson = "{\"name\": \"Updated Name\", \"email\": \"updated@example.com\"}";

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/users/1");
        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>("{}", HttpStatus.OK));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestForInvalidPatchRequest() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        String invalidPatchJson = "{\"name\": \"\", \"email\": \"not-an-email\"}";

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPatchJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/users/1");
        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>("{}", HttpStatus.OK));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetUserByIdSuccessfully() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/users/1");
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>("{\"id\":1, \"name\":\"John Doe\", \"email\":\"john@example.com\"}", HttpStatus.OK));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1, \"name\":\"John Doe\", \"email\":\"john@example.com\"}"));
    }


    @Test
    void shouldReturnAllUsers() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/users/");
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>("[{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\"}]", HttpStatus.OK));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\"}]"));
    }

    @Test
    void shouldHandleEmptyUserList() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/users/");
        when(restTemplate.getForEntity(anyString(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>("[]", HttpStatus.OK));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
