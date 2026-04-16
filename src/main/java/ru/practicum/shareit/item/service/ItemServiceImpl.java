package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public ItemDto saveItem(Long userId, NewItemRequest request) {
        log.info("Создание вещи пользователем id={}", userId);
        if (userId == null) {
            log.warn("Вещь без userId");
            throw new ValidationException("Id пользователя должно быть указано");
        }
        User user = findUserOrThrow(userId);
        Item item = ItemMapper.mapToItem(request, user);
        item = itemRepository.save(item);
        log.info("Вещь создана id={}, userId={}", item.getId(), userId);
        return ItemMapper.mapToItemDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request) {
        log.info("Обновление вещи id={} пользователем id={}", itemId, userId);
        findUserOrThrow(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Вещь не найдена id={}", itemId);
                    return new NotFoundException("Вещь с id " + itemId + " не найдена");
                });
        if (!item.getOwner().getId().equals(userId)) {
            log.warn("Пользователь id={} не владелец вещи id={}", userId, itemId);
            throw new ValidationException("Пользователь не является владельцем вещи");
        }

        ItemMapper.updateItemFields(item, request);
        itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemOwnerDto findItemById(Long itemId, Long userId) {
        log.debug("Получение вещи id={}", itemId);
        LocalDateTime now = LocalDateTime.now();
        Item item = findItemOrThrow(itemId);

        List<CommentDto> commentDtoList = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::mapToCommentDto)
                .toList();

        if (!item.getOwner().getId().equals(userId)) {
            return ItemMapper.mapToItemOwnerDto(item, null, null, commentDtoList);
        }

        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeOrderByStartDesc(itemId, now)
                .orElse(null);
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, now)
                .orElse(null);

        return ItemMapper.mapToItemOwnerDto(item, lastBooking, nextBooking, commentDtoList);
    }

    @Override
    public List<ItemOwnerDto> findAllUserItems(Long userId) {
        log.debug("Получение всех вещей пользователя id={}", userId);
        findUserOrThrow(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        Map<Long, Booking> lastBookings = bookingRepository.findLastBookings(itemIds, now).stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(), Function.identity()));

        Map<Long, Booking> nextBookings = bookingRepository.findNextBookings(itemIds, now).stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(), Function.identity()));

        Map<Long, List<CommentDto>> commentsByItemId = commentRepository
                .findAllByItemIdInOrderByDateOfCommentAsc(itemIds).stream()
                .collect(Collectors.groupingBy(
                        c -> c.getItem().getId(),
                        Collectors.mapping(CommentMapper::mapToCommentDto, Collectors.toList())
                ));

        return items.stream()
                .map(item -> ItemMapper.mapToItemOwnerDto(
                        item, lastBookings.get(item.getId()), nextBookings.get(item.getId()), commentsByItemId.get(item.getId())))
                .toList();
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.debug("Поиск вещей по тексту='{}'", text);
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto saveComment(Long userId, Long itemId, NewCommentRequest request) {
        User user = findUserOrThrow(userId);
        Item item = findItemOrThrow(itemId);

        boolean hasBooking = bookingRepository
                .existsByBookerIdAndItemIdAndStatusAndEndBefore(
                        userId,
                        itemId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now()
                );

        if (!hasBooking) {
            throw new ValidationException("Пользователь не может оставить комментарий");
        }

        Comment comment = CommentMapper.mapToComment(user, item, request);
        comment = commentRepository.save(comment);

        return CommentMapper.mapToCommentDto(comment);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден id={}", id);
                    return new NotFoundException("Пользователь с id " + id + " не найден");
                });
    }

    private Item findItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Вещь не найдена id={}", id);
                    return new NotFoundException("Вещь с id " + id + " не найдена");
                });
    }
}
