package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class MemoryItemRepository implements ItemRepository {
    private final Map<Long, List<Item>> usersItems = new HashMap<>();
    private final Map<Long, Item> items = new HashMap<>();
    private Long newId = 1L;

    @Override
    public Item saveItem(Long userId, Item item) {
        item.setId(newId++);
        items.put(item.getId(), item);
        List<Item> itemList = usersItems.getOrDefault(userId, new ArrayList<>());
        itemList.add(item);
        usersItems.put(userId, itemList);
        return item;
    }

    @Override
    public Item updateItem(Long userId, Item item) {
        List<Item> itemList = usersItems.get(userId);
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getId().equals(item.getId())) {
                itemList.set(i, item);
                break;
            }
        }

        items.put(item.getId(), item);
        usersItems.put(userId, itemList);

        return item;
    }

    @Override
    public Optional<Item> findItemById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> findAllUserItems(Long userId) {
        return usersItems.getOrDefault(userId, new ArrayList<>());
    }

    @Override
    public List<Item> searchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> availableItems = new ArrayList<>();
        for (Map.Entry<Long, Item> entry : items.entrySet()) {
            if (entry.getValue().getAvailable()) {
                availableItems.add(entry.getValue());
            }
        }
        return availableItems.stream()
                .filter(item -> item.getName().equalsIgnoreCase(text) ||
                        item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }
}
