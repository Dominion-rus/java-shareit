package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void createItemRequest_ShouldReturn201() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(null, "Need a laptop", LocalDateTime.now());
        ItemRequestDto responseDto = new ItemRequestDto(1L, "Need a laptop", LocalDateTime.now());

        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Need a laptop")));
    }

    @Test
    void getUserRequests_ShouldReturn200() throws Exception {
        List<ItemRequestResponseDto> requests = List.of(
                new ItemRequestResponseDto(1L, "Need a laptop", LocalDateTime.now(), List.of())
        );

        when(itemRequestService.getUserItemRequests(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].description", is("Need a laptop")));
    }

    @Test
    void getAllRequests_ShouldReturn200() throws Exception {
        List<ItemRequestResponseDto> requests = List.of(
                new ItemRequestResponseDto(1L, "Need a laptop", LocalDateTime.now(), List.of())
        );

        when(itemRequestService.getAllItemRequests(anyLong(), any(Pageable.class))).thenReturn(requests);

        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].description", is("Need a laptop")));
    }

    @Test
    void getRequestById_ShouldReturn200() throws Exception {
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto(1L, "Need a laptop",
                LocalDateTime.now(), List.of());

        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Need a laptop")));
    }

    @Test
    void getRequestById_ShouldReturn404_WhenNotFound() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Запрос не найден"));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createItemRequest_ShouldReturn500_WhenUserIdHeaderMissing() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(null, "Need a laptop", LocalDateTime.now());

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllRequests_ShouldUseDefaultPagination_WhenNoParamsProvided() throws Exception {
        List<ItemRequestResponseDto> requests = List.of(
                new ItemRequestResponseDto(1L, "Need a laptop", LocalDateTime.now(), List.of())
        );

        when(itemRequestService.getAllItemRequests(anyLong(), any(Pageable.class))).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].description", is("Need a laptop")));
    }

    @Test
    void getRequestById_ShouldReturn404_WhenRequestDoesNotExist() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Item request not found"));

        mockMvc.perform(get("/requests/999")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllRequests_ShouldReturn500_WhenInvalidPaginationParameters() throws Exception {
        mockMvc.perform(get("/requests/all?from=-1&size=-10")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isInternalServerError());
    }




}

