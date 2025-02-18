package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemPatchDto itemPatchDto);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getUserItems(Long userId);

    List<ItemDto> searchItems(String text);
}
