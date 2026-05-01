package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get all users items userId={}", userId);
        return itemClient.getAllUserItem(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable Long id) {
        log.info("Get item id={}, userId={}", id, userId);
        return itemClient.getItemById(userId, id);
    }

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody NewItemRequest request) {
        log.info("Create item {}", request);
        return itemClient.createItem(userId, request);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable Long id,
                                             @RequestBody UpdateItemRequest request) {
        log.info("Update item id={}, userId={}", id, userId);
        return itemClient.updateItem(userId, id, request);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text) {
        log.info("Search item {}", text);
        return itemClient.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComments(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable Long itemId,
                                               @Valid @RequestBody NewCommentRequest request) {
        log.info("Create comment itemId={}, userId={}", itemId, userId);
        return itemClient.createComment(userId, itemId, request);
    }
}
