package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NoSuchItemException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component("MemoryItemStorage")
public class MemoryItemStorage implements ItemStorage {
    private final HashMap<Long, Item> storage = new HashMap<>();

    @Override
    public Item save(Item item) {
        log.debug("Adding item {} to storage", item.getName());
        item.setId(generateId());
        storage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        log.debug("Updating item {} in storage", item.getId());
        find(item.getId());
        return storage.put(item.getId(), item);
    }

    @Override
    public Item delete(Long id) {
        log.debug("Deleting item {} from storage", id);
        return Optional.ofNullable(storage.remove(id)).orElseThrow(() -> {
            log.error("Incorrect item id: " + id);
            throw new NoSuchItemException("Incorrect id");
        });
    }

    @Override
    public Item find(Long id) {
        log.debug("Finding item {} from storage", id);
        return Optional.ofNullable(storage.get(id)).orElseThrow(() -> {
            log.error("Incorrect item id: " + id);
            throw new NoSuchItemException("Incorrect id");
        });
    }

    @Override
    public Collection<Item> findAll() {
        log.debug("Requesting all items from storage");
        return storage.values();
    }

    @Override
    public Collection<Item> findAllByOwner(Long id) {
        log.debug("Getting items of user {} from storage", id);
        return storage.values().stream()
                .filter(item -> item.getOwnerId().equals(id))
                .collect(Collectors.toList());
    }

    private long generateId() {
        log.debug("Generating id for item");
        long currId = storage.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currId;
    }
}