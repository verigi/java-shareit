package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.comment.CommentCreateDto;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Creating item, userId={}", userId);
        return itemClient.createItem(userId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody ItemUpdateDto itemUpdateDto) {
        log.info("Updating item {}, userId={}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemUpdateDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId) {
        log.info("Deleting item {}, userId={}", itemId, userId);
        return itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItem(@PathVariable long itemId) {
        log.info("Finding item with id={}", itemId);
        return itemClient.findItem(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Finding all items for userId={}", userId);
        return itemClient.findAllByOwner(userId);
    }

    @GetMapping("/by-request/{requestId}")
    public ResponseEntity<Object> findAllByRequestId(@PathVariable long requestId) {
        log.info("Finding all items by requestId={}", requestId);
        return itemClient.findAllByRequestId(requestId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("Searching items with text={}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Adding comment for itemId={}, userId={}", itemId, userId);
        return itemClient.addComment(itemId, userId, commentCreateDto);
    }
}