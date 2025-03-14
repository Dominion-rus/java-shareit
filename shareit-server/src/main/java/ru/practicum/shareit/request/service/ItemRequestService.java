package ru.practicum.shareit.request.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> getUserItemRequests(Long userId);

    List<ItemRequestResponseDto> getAllItemRequests(Long userId, Pageable pageable);

    ItemRequestResponseDto getItemRequestById(Long userId, Long requestId);
}
