package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public ItemDto toDto(Item item) {
        if (item == null) return null;

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .build();
    }

    public Item toItem(Long ownerId, ItemDto itemDto) {
        if (itemDto == null) return null;

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .isAvailable(itemDto.getAvailable())
                .ownerId(ownerId)
                .requestId(itemDto.getRequestId())
                .build();
    }

    public Collection<ItemDto> toDtoList(List<Item> items) {
        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Collection<Item> toItems(List<ItemDto> dtos, Long ownerId) {
        return dtos.stream()
                .map(dto -> this.toItem(ownerId, dto))
                .collect(Collectors.toList());
    }

    public Item updateItemFromDto(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setIsAvailable(itemDto.getAvailable());

        return item;
    }
}
