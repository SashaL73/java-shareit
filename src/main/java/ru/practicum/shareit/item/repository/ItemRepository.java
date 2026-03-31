package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item saveItem(Long userId, Item item);

    Item updateItem(Long userId, Item item);

    Optional<Item> findItemById(Long itemId);

    List<Item> findAllUserItems(Long userId);

    List<Item> searchItem(String text);

}
