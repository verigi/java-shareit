package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper mapper;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage, ItemMapper mapper) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.mapper = mapper;
    }


    @Override
    public ItemDto save(Long ownerId, ItemDto itemDto) {
        log.debug("Save item request received. Item name: {}, owner id: {}", itemDto.getName(), ownerId);

        User user = userStorage.find(ownerId);
        Item item = mapper.toItem(ownerId, itemDto);

        log.debug("Saving successful!");
        return mapper.toDto(itemStorage.save(item));
    }

    @Override
    public ItemDto update(Long ownerId, ItemDto itemDto) {
        log.debug("Update item request received. Item id: {}, owner id: {}", itemDto.getId(), ownerId);

        Item item = itemStorage.find(itemDto.getId());
        if (!item.getOwnerId().equals(ownerId)) {
            log.error("Illegal update request");
            throw new ItemOwnerException("Only the owner can make updating");
        }

        Item updItem = itemStorage.update(mapper.updateItemFromDto(itemDto, item));
        log.debug("Updating successful!");
        return mapper.toDto(updItem);
    }

    @Override
    public ItemDto delete(Long ownerId, Long itemId) {
        log.debug("Delete item request received. Item id: {}, owner id: {}", itemId, ownerId);
        Item item = itemStorage.find(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            log.warn("Illegal deleting request");
            throw new ItemOwnerException("Only the owner can delete the item");
        }
        log.debug("Deleting successful!");
        return mapper.toDto(itemStorage.delete(itemId));
    }

    @Override
    public ItemDto find(Long itemId) {
        log.debug("Get item request received. Item id: {}", itemId);
        return mapper.toDto(itemStorage.find(itemId));
    }

    @Override
    public Collection<ItemDto> findAll() {
        log.debug("Get all items request received");
        return mapper.toDtoList(new ArrayList<>(itemStorage.findAll()));
    }

    @Override
    public Collection<ItemDto> findAllByOwner(Long ownerId) {
        log.debug("Get all items of the owner request received");
        return mapper.toDtoList(new ArrayList<>(itemStorage.findAllByOwner(ownerId)));
    }

    @Override
    public Collection<ItemDto> search(String text) {
        log.debug("Search item by name/description request received");

        if (text == null || text.trim().isEmpty()) {
            log.debug("No criteria for search provided");
            return Collections.emptyList();
        }
        String lowCaseText = text.toLowerCase();

        return mapper.toDtoList(itemStorage.findAll().stream()
                .filter(item -> item.getIsAvailable().equals(Boolean.TRUE) &&
                        (item.getName().toLowerCase().contains(lowCaseText) ||
                                item.getDescription().toLowerCase().contains(lowCaseText)))
                .collect(Collectors.toList()));
    }
}