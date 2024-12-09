package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExpandedDto;

import java.util.Collection;

public interface ItemService {
    ItemDto saveItem(Long ownerId, ItemCreateDto itemDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    ItemDto deleteItem(Long ownerId, Long itemId);

    ItemExpandedDto findItem(Long itemId);

    Collection<ItemExpandedDto> findAllByOwner(Long ownerId);

    Collection<ItemExpandedDto> findAllByRequestId(Long requestId);

    Collection<ItemDto> search(String text);

    CommentDto addComment(Long itemId, Long userId, CommentCreateDto commentDto);
}