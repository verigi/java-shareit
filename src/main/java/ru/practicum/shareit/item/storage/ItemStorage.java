package ru.practicum.shareit.item.storage;


import ru.practicum.shareit.item.entity.Item;

import java.util.Collection;

public interface ItemStorage {
    Item save(Item item);

    Item update(Item item);

    Item delete(Long id);

    Item find(Long id);

    Collection<Item> findAll();

    Collection<Item> findAllByOwner(Long id);
}