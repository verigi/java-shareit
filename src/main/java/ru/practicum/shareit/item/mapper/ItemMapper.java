package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExpandedDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    CommentMapper commentMapper;

    @Autowired
    public ItemMapper(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    public ItemDto toDto(Item item) {
        return item == null ? null : ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .build();
    }

    public ItemExpandedDto toExpandedDto(Item item,
                                         List<Comment> comments,
                                         Optional<LocalDateTime> lastBooking,
                                         Optional<LocalDateTime> nextBooking) {
        return item == null ? null : ItemExpandedDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking.orElse(null))
                .nextBooking(nextBooking.orElse(null))
                .comments(comments.stream().map(commentMapper::toDto).collect(Collectors.toList()))
                .build();
    }

    public ItemExpandedDto toExpandedDto(Item item,
                                         List<Comment> comments) {
        if (item == null) return null;
        ItemExpandedDto itemExpandedDto = ItemExpandedDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments.stream().map(comment -> commentMapper.toDto(comment)).toList())
                .ownerId(item.getOwner().getId())
                .build();

        if (item.getRequest() != null) {
            itemExpandedDto.setRequestId(item.getRequest().getId());
        }
        return itemExpandedDto;
    }

    public Item toItem(ItemCreateDto item, User owner) {
        return item == null ? null : Item.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(owner)
                .build();
    }

    public Collection<ItemDto> toDtoList(List<Item> items) {
        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    public Item updateItemFromDto(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        return item;
    }
}