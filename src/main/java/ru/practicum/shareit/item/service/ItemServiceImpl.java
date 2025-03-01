package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.AccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item), null, null, null);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemPatchDto itemPatchDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Редактировать может только владелец вещи");
        }

        if (itemPatchDto.getName() != null) {
            item.setName(itemPatchDto.getName());
        }
        if (itemPatchDto.getDescription() != null) {
            item.setDescription(itemPatchDto.getDescription());
        }
        if (itemPatchDto.getAvailable() != null) {
            item.setAvailable(itemPatchDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(item), null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        return ItemMapper.toItemDto(item, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getUserItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        return items.stream()
                .map(item -> {

                    Booking lastBooking = bookingRepository
                            .findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
                    Booking nextBooking = bookingRepository
                            .findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());

                    List<Comment> comments = commentRepository.findByItemId(item.getId());

                    return ItemMapper.toItemDto(
                            item,
                            lastBooking != null ? List.of(BookingMapper
                                    .toBookingDto(lastBooking)) : Collections.emptyList(),  // Преобразуем в список
                            nextBooking != null ? List.of(BookingMapper
                                    .toBookingDto(nextBooking)) : Collections.emptyList(),
                            comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<Item> items = itemRepository.searchByNameOrDescription(text);

        return items.stream()
                .map(item -> {

                    Booking lastBooking = bookingRepository
                            .findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now());
                    Booking nextBooking = bookingRepository
                            .findFirstByItemIdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());

                    List<Comment> comments = commentRepository.findByItemId(item.getId());

                    return ItemMapper.toItemDto(
                            item,
                            lastBooking != null ? List.of(BookingMapper
                                    .toBookingDto(lastBooking)) : Collections.emptyList(),  // Преобразуем в список
                            nextBooking != null ? List.of(BookingMapper
                                    .toBookingDto(nextBooking)) : Collections.emptyList(),
                            comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemWithCommentsAndBookings(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(itemId);
        LocalDateTime now = LocalDateTime.now();

        boolean isOwner = item.getOwner().getId().equals(userId);

        List<Booking> pastBookings = bookings.stream()
                .filter(b -> b.getEnd().isBefore(now) && b.getStatus() == BookingStatus.APPROVED)
                .collect(Collectors.toList());

        // lastBooking видно только владельцу!
        List<BookingResponseDto> lastBooking = (isOwner && !pastBookings.isEmpty()) ?
                Collections.singletonList(BookingMapper.toBookingDto(pastBookings.get(0))) :
                Collections.emptyList();

        List<BookingResponseDto> nextBooking = bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .map(BookingMapper::toBookingDto)
                .findFirst()
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
    }





}


