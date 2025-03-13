package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(null, "John Doe", "john@example.com"));
        itemRequestDto = new ItemRequestDto(null, "Need a laptop", LocalDateTime.now());
    }

    @Test
    void createItemRequest_ShouldSaveToDatabase() {
        ItemRequestDto savedRequest = itemRequestService.createItemRequest(user.getId(), itemRequestDto);

        assertThat(savedRequest, notNullValue());
        assertThat(savedRequest.getId(), notNullValue());
        assertThat(savedRequest.getDescription(), is("Need a laptop"));
    }

    @Test
    void getUserItemRequests_ShouldReturnList() {
        itemRequestService.createItemRequest(user.getId(), itemRequestDto);
        List<ItemRequestResponseDto> requests = itemRequestService.getUserItemRequests(user.getId());

        assertThat(requests, hasSize(1));
        assertThat(requests.get(0).getDescription(), is("Need a laptop"));
    }
}

