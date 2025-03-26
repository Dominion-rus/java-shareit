package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john@example.com");
        itemRequest = new ItemRequest(1L, "Need a laptop", user, LocalDateTime.now());
        itemRequestDto = new ItemRequestDto(null, "Need a laptop", LocalDateTime.now());
    }

    @Test
    void createItemRequest_ShouldReturnItemRequestDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createItemRequest(user.getId(), itemRequestDto);

        assertThat(result, notNullValue());
        assertThat(result.getDescription(), is("Need a laptop"));
    }

    @Test
    void createItemRequest_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.createItemRequest(user.getId(), itemRequestDto)
        );

        assertThat(exception.getMessage(), is("Пользователь не найден"));
    }

    @Test
    void getUserItemRequests_ShouldReturnRequestsList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));

        List<ItemRequestResponseDto> result = itemRequestService.getUserItemRequests(user.getId());

        assertThat(result, not(empty()));
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getDescription(), is("Need a laptop"));
    }

    @Test
    void getUserItemRequests_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getUserItemRequests(user.getId())
        );

        assertThat(exception.getMessage(), is("Пользователь не найден"));
    }

    @Test
    void getUserItemRequests_WhenNoRequests_ShouldReturnEmptyList() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        List<ItemRequestResponseDto> result = itemRequestService.getUserItemRequests(user.getId());

        assertThat(result, is(empty()));
    }

    @Test
    void getAllItemRequests_ShouldReturnRequestsList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(anyLong(), eq(pageable)))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestResponseDto> result = itemRequestService.getAllItemRequests(user.getId(), pageable);

        assertThat(result, not(empty()));
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getDescription(), is("Need a laptop"));
    }

    @Test
    void getAllItemRequests_WhenUserNotFound_ShouldThrowException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getAllItemRequests(user.getId(), pageable)
        );

        assertThat(exception.getMessage(), is("Пользователь не найден"));
    }

    @Test
    void getAllItemRequests_WhenNoRequests_ShouldReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(anyLong(), eq(pageable)))
                .thenReturn(Collections.emptyList());

        List<ItemRequestResponseDto> result = itemRequestService.getAllItemRequests(user.getId(), pageable);

        assertThat(result, is(empty()));
    }

    @Test
    void getItemRequestById_ShouldReturnRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequestResponseDto result = itemRequestService.getItemRequestById(user.getId(), itemRequest.getId());

        assertThat(result, notNullValue());
        assertThat(result.getDescription(), is("Need a laptop"));
    }

    @Test
    void getItemRequestById_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequestById(user.getId(), itemRequest.getId())
        );

        assertThat(exception.getMessage(), is("Пользователь не найден"));
    }

    @Test
    void getItemRequestById_WhenRequestNotFound_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequestById(user.getId(), itemRequest.getId())
        );

        assertThat(exception.getMessage(), is("Запрос не найден"));
    }
}
