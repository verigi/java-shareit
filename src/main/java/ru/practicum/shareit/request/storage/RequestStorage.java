package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface RequestStorage {
    ItemRequest save(ItemRequest request);
    ItemRequest update(ItemRequest request);
    ItemRequest delete(Long id);
    ItemRequest find(Long id);
    Collection<ItemRequest> findByRequester(Long id);
    Collection<ItemRequest> findAll();
}
