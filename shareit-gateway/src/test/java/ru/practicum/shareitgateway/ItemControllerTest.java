package ru.practicum.shareitgateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitgateway.config.AppConfig;
import ru.practicum.shareitgateway.item.ItemController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private ItemController itemController;

    @Test
    void shouldReturnBadRequestForInvalidItem() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();

        String invalidItemJson = "{\"name\": \"\", \"description\": \"\", \"available\": null}";

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidItemJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateItemSuccessfully() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();

        String validItemJson = "{\"name\": \"Laptop\", \"description\": \"Powerful laptop\", \"available\": true}";

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/items");
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validItemJson))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateItemSuccessfully() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();

        String updateItemJson = "{\"name\": \"Updated Laptop\", \"description\": \"More powerful\", \"available\": true}";

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/items/1");
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateItemJson))
                .andExpect(status().isOk());
    }



    @Test
    void shouldGetItemSuccessfully() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/items/1");
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetUserItemsSuccessfully() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();

        when(appConfig.getFullUrl(anyString())).thenReturn("http://localhost:8080/items");
        when(restTemplate.exchange(anyString(), any(), any(), any(Class.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
    }

}
