package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody ItemDto itemDto) {
        log.info("Received POST request");
        ItemDto createdItem = itemService.save(userId, itemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto) {
        log.info("Received PATCH request");
        itemDto.setId(itemId);
        itemDto.setOwnerId(userId);
        ItemDto updatedItem = itemService.update(userId, itemDto);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long itemId) {
        log.info("Received DELETE request");
        itemService.delete(ownerId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        log.info("Received GET request, single item");
        ItemDto item = itemService.find(itemId);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received GET request, full item list");
        Collection<ItemDto> items = itemService.findAllByOwner(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItems(@RequestParam("text") String text) {
        log.info("Received GET request, search items");
        Collection<ItemDto> items = itemService.search(text);
        return ResponseEntity.ok(items);
    }
}