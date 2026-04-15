package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(Long userId, NewItemRequest request);

    ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request);

    ItemOwnerDto findItemById(Long itemId, Long userId);

    List<ItemOwnerDto> findAllUserItems(Long userId);

    List<ItemDto> searchItem(String text);

    CommentDto saveComment(Long userId, Long itemId, NewCommentRequest request);

}
