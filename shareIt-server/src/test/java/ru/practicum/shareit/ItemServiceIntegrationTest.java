package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(null, "John Doe", "john@example.com"));
        itemDto = new ItemDto(null, "Laptop", "Powerful laptop", true, null,
                null, null, null);
    }

    @Test
    void createItem_ShouldSaveToDatabase() {
        ItemDto savedItem = itemService.createItem(user.getId(), itemDto);

        assertThat(savedItem, notNullValue());
        assertThat(savedItem.getId(), notNullValue());
        assertThat(savedItem.getName(), equalTo("Laptop"));
        assertThat(savedItem.getDescription(), containsString("Powerful"));
    }

    @Test
    void getUserItems_ShouldReturnList() {
        itemService.createItem(user.getId(), itemDto);
        List<ItemDto> items = itemService.getUserItems(user.getId());

        assertThat(items, hasSize(1));
        assertThat(items.get(0).getName(), equalTo("Laptop"));
    }
}

