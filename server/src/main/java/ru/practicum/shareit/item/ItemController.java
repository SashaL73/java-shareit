package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemOwnerDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findAllUserItems(userId);
    }

    @GetMapping("/{id}")
    public ItemOwnerDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long id) {
        return itemService.findItemById(id, userId);
    }

    @PostMapping
    public ItemDto saveItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody NewItemRequest request) {
        return itemService.saveItem(userId, request);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable Long id,
                              @RequestBody UpdateItemRequest request) {
        return itemService.updateItem(userId, id, request);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComments(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable Long itemId,
                                   @Valid @RequestBody NewCommentRequest request) {
        return itemService.saveComment(userId, itemId, request);
    }

}
