package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private User anotherUser;
    private Item item;
    private ItemDto itemDto;
    private ItemPatchDto itemPatchDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john@example.com");
        anotherUser = new User(2L, "Alice Smith", "alice@example.com");
        item = new Item(1L, "Laptop", "Powerful laptop", true, user, null);
        itemDto = new ItemDto(1L, "Laptop", "Powerful laptop", true, null,
                null, null, null);
        itemPatchDto = new ItemPatchDto("Updated Laptop", "New powerful laptop", false,
                null);
    }

    @Test
    void createItem_ShouldReturnItemDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(Mockito.<Item>any())).thenReturn(item);


        ItemDto result = itemService.createItem(user.getId(), itemDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), is("Laptop"));
        assertThat(result.getDescription(), containsString("Powerful"));
    }

    @Test
    void createItem_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.createItem(user.getId(), itemDto)
        );

        assertThat(exception.getMessage(), is("Пользователь не найден"));
    }

    @Test
    void updateItem_WhenNotOwner_ShouldThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> itemService.updateItem(anotherUser.getId(), item.getId(), itemPatchDto)
        );

        assertThat(exception.getMessage(), is("Редактировать может только владелец вещи"));
    }

    @Test
    void updateItem_WhenRequestNotFound_ShouldThrowException() {
        itemPatchDto.setRequestId(1L);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(user.getId(), item.getId(), itemPatchDto)
        );

        assertThat(exception.getMessage(), is("Запрос не найден"));
    }

    @Test
    void getItemById_ShouldReturnItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto result = itemService.getItemById(item.getId());

        assertThat(result, notNullValue());
        assertThat(result.getName(), is("Laptop"));
    }

    @Test
    void getItemById_WhenItemNotFound_ShouldThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(1L)
        );

        assertThat(exception.getMessage(), is("Вещь не найдена"));
    }

    @Test
    void getUserItems_ShouldReturnItemsList() {
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getUserItems(user.getId());

        assertThat(result, not(empty()));
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getName(), is("Laptop"));
    }

    @Test
    void getUserItems_WhenNoItems_ShouldReturnEmptyList() {
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of());

        List<ItemDto> result = itemService.getUserItems(user.getId());

        assertThat(result, empty());
    }

    @Test
    void searchItems_ShouldReturnMatchingItems() {
        when(itemRepository.searchByNameOrDescription(anyString())).thenReturn(List.of(item));

        List<ItemDto> result = itemService.searchItems("Laptop");

        assertThat(result, not(empty()));
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getName(), is("Laptop"));
    }

    @Test
    void searchItems_WhenEmptyQuery_ShouldReturnEmptyList() {
        List<ItemDto> result = itemService.searchItems("");

        assertThat(result, empty());
    }
}

