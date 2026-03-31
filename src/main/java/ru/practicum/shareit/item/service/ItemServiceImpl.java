package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto saveItem(Long userId, NewItemRequest request) {
        log.info("Создание вещи пользователем id={}", userId);
        if (userId == null) {
            log.warn("Вещь без userId");
            throw new ValidationException("Id пользователя должно быть указано");
        }
        User user = findUserOrThrow(userId);
        Item item = ItemMapper.mapToItem(request, user);
        item = itemRepository.saveItem(userId, item);
        log.info("Вещь создана id={}, userId={}", item.getId(), userId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request) {
        log.info("Обновление вещи id={} пользователем id={}", itemId, userId);
        findUserOrThrow(userId);
        Item item = itemRepository.findItemById(itemId)
                .orElseThrow(() -> {
                    log.warn("Вещь не найдена id={}", itemId);
                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
                });
        if (!item.getOwner().getId().equals(userId)) {
            log.warn("Пользователь id={} не владелец вещи id={}", userId, itemId);
            throw new ValidationException("Пользователь не является владельцем вещи");
        }

        Item updatedItem = ItemMapper.updateItemFields(item, request);
        itemRepository.updateItem(userId, updatedItem);
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        log.debug("Получение вещи id={}", itemId);
        Item item = itemRepository.findItemById(itemId)
                .orElseThrow(() -> {
                    log.warn("Вещь не найдена id={}", itemId);
                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
                });
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> findAllUserItems(Long userId) {
        log.debug("Получение всех вещей пользователя id={}", userId);
        findUserOrThrow(userId);
        return itemRepository.findAllUserItems(userId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.debug("Поиск вещей по тексту='{}'", text);
        return itemRepository.searchItem(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    private User findUserOrThrow(Long id) {
        return userRepository.getUserById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден id={}", id);
                    return new NotFoundException("Пользователь с id " + id + " не найден");
                });
    }
}
