package ru.practicum.shareit.request.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NoSuchRequestException;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component("MemoryRequestStorage")
public class MemoryRequestStorage implements RequestStorage {
    final HashMap<Long, ItemRequest> storage = new HashMap<>();

    @Override
    public ItemRequest save(ItemRequest request) {
        log.debug("Adding request from user " + request.getRequestor());
        request.setId(generateId());
        return storage.put(request.getId(), request);
    }

    @Override
    public ItemRequest update(ItemRequest request) {
        log.debug("Updating request " + request.getId());
        find(request.getId());
        return storage.put(request.getId(), request);
    }

    @Override
    public ItemRequest delete(Long id) {
        log.debug("Deleting request " + id);
        return Optional.ofNullable(storage.remove(id)).orElseThrow(() -> {
            log.warn("Incorrect request id: " + id);
            throw new NoSuchRequestException("Incorrect id");
        });
    }

    @Override
    public ItemRequest find(Long id) {
        log.debug("Getting request " + id);
        return Optional.ofNullable(storage.get(id)).orElseThrow(() -> {
            log.warn("Incorrect request id: " + id);
            throw new NoSuchRequestException("Incorrect id");
        });
    }

    @Override
    public Collection<ItemRequest> findByRequester(Long id) {
        log.debug("Getting requests of user " + id);
        return storage.values().stream()
                .filter(request -> request.getRequestor().getId().equals(id))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequest> findAll() {
        return storage.values();
    }

    private long generateId() {
        log.debug("Generating id...");
        long currId = storage.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currId;
    }
}
