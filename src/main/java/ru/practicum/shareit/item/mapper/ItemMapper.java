package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item, List<BookingResponseDto> lastBooking, List<BookingResponseDto> nextBooking, List<CommentDto> comments) {

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking != null && !lastBooking.isEmpty() ? lastBooking.get(0) : null,
                nextBooking != null && !nextBooking.isEmpty() ? nextBooking.get(0) : null,
                comments
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null, // Owner (владелец) устанавливается отдельно в сервисе
                null  // Request (запрос) тоже устанавливается отдельно
        );
    }
}
