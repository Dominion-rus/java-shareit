package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getUserItemRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);

        return requests.stream().map(request -> {
            List<ItemDto> items = itemRepository.findByRequestId(request.getId())
                    .stream()
                    .map(item -> ItemMapper.toItemDto(item, null, null, null))
                    .collect(Collectors.toList());

            return ItemRequestMapper.toItemRequestResponseDto(request, items);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getAllItemRequests(Long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, pageable);

        return requests.stream().map(request -> {
            List<ItemDto> items = itemRepository.findByRequestId(request.getId())
                    .stream()
                    .map(item -> ItemMapper.toItemDto(item, null, null, null))
                    .collect(Collectors.toList());

            return ItemRequestMapper.toItemRequestResponseDto(request, items);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestResponseDto getItemRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        List<ItemDto> items = itemRepository.findByRequestId(request.getId())
                .stream()
                .map(item -> ItemMapper.toItemDto(item, null, null, null))
                .collect(Collectors.toList());

        return ItemRequestMapper.toItemRequestResponseDto(request, items);
    }
}
