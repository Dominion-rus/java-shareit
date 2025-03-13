package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void createItem_ShouldReturn201() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Laptop", "Powerful laptop",
                true, null, null, null, null);
        ItemDto responseDto = new ItemDto(1L, "Laptop", "Powerful laptop",
                true, null, null, null, null);

        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")));
    }

    @Test
    void updateItem_ShouldReturn200() throws Exception {
        ItemPatchDto itemPatchDto = new ItemPatchDto("Updated Laptop", null, false,
                null);
        ItemDto updatedItemDto = new ItemDto(1L, "Updated Laptop", "Powerful laptop",
                true, null, null, null, null);

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemPatchDto.class))).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(itemPatchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Laptop")));
    }

    @Test
    void updateItem_ShouldReturn403_WhenNotOwner() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemPatchDto.class)))
                .thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "2")
                        .content(objectMapper.writeValueAsString(new ItemPatchDto("Updated Laptop",
                                null, false,
                                null))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getItem_ShouldReturn200() throws Exception {
        ItemDto responseDto = new ItemDto(1L, "Laptop", "Powerful laptop",
                true, null, null, null, null);

        when(itemService.getItemWithCommentsAndBookings(anyLong(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")));
    }

    @Test
    void getItem_ShouldReturn404_WhenNotFound() throws Exception {
        when(itemService.getItemWithCommentsAndBookings(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserItems_ShouldReturn200() throws Exception {
        List<ItemDto> items = List.of(
                new ItemDto(1L, "Laptop", "Powerful laptop",
                        true, null, null, null, null)
        );

        when(itemService.getUserItems(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    void searchItems_ShouldReturn200() throws Exception {
        List<ItemDto> items = List.of(
                new ItemDto(1L, "Laptop", "Powerful laptop",
                        true, null, null, null, null)
        );

        when(itemService.searchItems(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search?text=laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")));
    }

    @Test
    void addComment_ShouldReturn200() throws Exception {
        CommentDto commentDto = new CommentDto(null, "Great product!", "User1", LocalDateTime.now());
        CommentDto responseDto = new CommentDto(1L, "Great product!", "User1", LocalDateTime.now());

        when(commentService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Great product!")));
    }


    @Test
    void addComment_ShouldReturn403_WhenNotRenter() throws Exception {
        when(commentService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(new CommentDto(null, "Nice!",
                                "User1", LocalDateTime.now()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void createItem_ShouldReturn500_WhenUserIdHeaderMissing() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Laptop", "Powerful laptop",
                true, null, null, null, null);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateItem_ShouldReturn404_WhenItemNotFound() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemPatchDto.class)))
                .thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(patch("/items/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(new ItemPatchDto("Updated Name",
                                null, false, null))))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserItems_ShouldReturn500_WhenUserIdHeaderMissing() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenTextIsEmpty() throws Exception {
        when(itemService.searchItems(anyString())).thenReturn(List.of());

        mockMvc.perform(get("/items/search?text="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    void addComment_ShouldReturn404_WhenItemNotFound() throws Exception {
        when(commentService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(post("/items/999/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(new CommentDto(null, "Nice!",
                                "User1", LocalDateTime.now()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void addComment_ShouldReturn500_WhenTextIsEmpty() throws Exception {
        CommentDto commentDto = new CommentDto(null, "", "User1", LocalDateTime.now());

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isInternalServerError());
    }
}

