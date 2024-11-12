package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto save(Long ownerId, ItemDto itemDto);

    ItemDto update(Long ownerId, ItemDto itemDto);

    ItemDto delete(Long ownerId, Long itemId);

    ItemDto find(Long itemId);

    Collection<ItemDto> findAll();

    Collection<ItemDto> findAllByOwner(Long ownerId);

    Collection<ItemDto> search(String text);
}
