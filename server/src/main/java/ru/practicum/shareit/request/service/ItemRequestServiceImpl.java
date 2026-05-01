package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, NewItemRequestDto requestDto) {
        log.info("Создание запроса пользователем id={}", userId);
        User user = findUserOrThrow(userId);
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(requestDto, user);
        itemRequest = requestRepository.save(itemRequest);
        log.info("Запрос создан id={}, userId={}", itemRequest.getId(), userId);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest, null);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        log.debug("Получение всех запросов пользователем id={}", userId);
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        List<Long> itemsId = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemRepository.findRequestItems(itemsId);

        Map<Long, List<Item>> itemsMap = items.stream()
                .collect(Collectors.groupingBy(i -> i.getRequest().getId()));


        return itemRequests.stream()
                .map(req -> {
                    List<ItemDto> reqItems = itemsMap.getOrDefault(req.getId(), Collections.emptyList())
                            .stream()
                            .map(ItemMapper::mapToItemDto)
                            .toList();
                    return ItemRequestMapper.mapToItemRequestDto(req, reqItems);

                })
                .toList();
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId) {
        log.debug("Получение запроса id={}", requestId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        List<Item> items = itemRepository.findRequestItems(List.of(requestId));

        List<ItemDto> itemDtoList = items.stream()
                .map(ItemMapper::mapToItemDto)
                .toList();

        return ItemRequestMapper.mapToItemRequestDto(request, itemDtoList);


    }

    @Override
    public List<ItemRequestDto> getAllUserRequests(Long userId) {
        log.debug("Получение всех запросов пользователя id={}", userId);
        List<ItemRequest> itemRequests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);

        List<Long> requestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemRepository.findRequestItems(requestIds);

        Map<Long, List<Item>> itemsMap = items.stream()
                .collect(Collectors.groupingBy(i -> i.getRequest().getId()));

        return itemRequests.stream()
                .map(req -> {
                    List<ItemDto> reqItems = itemsMap.getOrDefault(req.getId(), Collections.emptyList())
                            .stream()
                            .map(ItemMapper::mapToItemDto)
                            .toList();
                    return ItemRequestMapper.mapToItemRequestDto(req, reqItems);
                })
                .toList();
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден id={}", id);
                    return new NotFoundException("Пользователь с id " + id + " не найден");
                });
    }
}
