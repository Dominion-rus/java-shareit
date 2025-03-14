package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void getItemById_ShouldReturnItem() {
        ItemDto savedItem = itemService.createItem(user.getId(), itemDto);
        ItemDto foundItem = itemService.getItemById(savedItem.getId());

        assertThat(foundItem, notNullValue());
        assertThat(foundItem.getId(), is(savedItem.getId()));
        assertThat(foundItem.getName(), is("Laptop"));
    }

    @Test
    void getItemById_ShouldThrowException_WhenNotFound() {
        Exception exception = assertThrows(NotFoundException.class, () ->
                itemService.getItemById(999L));

        assertThat(exception.getMessage(), containsString("Вещь не найдена"));
    }

    @Test
    void updateItem_ShouldModifyItemData() {
        ItemDto savedItem = itemService.createItem(user.getId(), itemDto);
        ItemPatchDto updateDto = new ItemPatchDto("Updated Laptop", "New Description", false, null);

        ItemDto updatedItem = itemService.updateItem(user.getId(), savedItem.getId(), updateDto);

        assertThat(updatedItem.getName(), is("Updated Laptop"));
        assertThat(updatedItem.getDescription(), is("New Description"));
        assertThat(updatedItem.getAvailable(), is(false));
    }

    @Test
    void searchItems_ShouldReturnMatchingItems() {
        itemService.createItem(user.getId(), itemDto);
        List<ItemDto> foundItems = itemService.searchItems("Laptop");

        assertThat(foundItems, hasSize(1));
        assertThat(foundItems.get(0).getName(), containsString("Laptop"));
    }

    @Test
    void deleteItem_ShouldRemoveItemFromDatabase() {
        ItemDto savedItem = itemService.createItem(user.getId(), itemDto);
        itemRepository.deleteById(savedItem.getId());

        Exception exception = assertThrows(NotFoundException.class, () ->
                itemService.getItemById(savedItem.getId()));

        assertThat(exception.getMessage(), containsString("Вещь не найдена"));
    }

    @Test
    void createItem_ShouldThrowException_WhenUserNotFound() {
        Exception exception = assertThrows(NotFoundException.class, () ->
                itemService.createItem(999L, itemDto));

        assertThat(exception.getMessage(), containsString("Пользователь не найден"));
    }
}
