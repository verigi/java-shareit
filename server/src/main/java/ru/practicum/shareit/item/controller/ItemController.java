package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExpandedDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @Valid @RequestBody ItemCreateDto item) {
        log.info("Received POST request to create item");
        ItemDto createdItem = service.saveItem(ownerId, item);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Received PATCH request to update item with id: {}", itemId);
        ItemDto updatedItem = service.updateItem(ownerId, itemId, itemDto);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ItemDto> delete(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long itemId) {
        log.info("DELETE request received. Owner id: {}, Item id: {}", ownerId, itemId);
        ItemDto deletedItem = service.deleteItem(ownerId, itemId);
        return ResponseEntity.ok(deletedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemExpandedDto> find(@PathVariable Long itemId) {
        log.info("GET request received. Item id: {}", itemId);
        ItemExpandedDto item = service.findItem(itemId);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemExpandedDto>> findAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("GET request received. Get all items by owner. Owner id: {}", ownerId);
        Collection<ItemExpandedDto> items = service.findAllByOwner(ownerId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/by-request/{requestId}")
    public ResponseEntity<Collection<ItemExpandedDto>> findAllByRequestId(@PathVariable Long requestId) {
        log.info("GET request received. Get all items by request id. Request id: {}", requestId);
        Collection<ItemExpandedDto> items = service.findAllByRequestId(requestId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItems(@RequestParam String text) {
        log.info("GET request received. Search items by text: {}", text);
        Collection<ItemDto> items = service.search(text);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody CommentCreateDto commentCreateDto) {
        log.info("POST request received. Add comment. Item id: {}, User id: {}", itemId, userId);
        CommentDto comment = service.addComment(itemId, userId, commentCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
}