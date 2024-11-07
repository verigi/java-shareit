package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {
    public ItemRequestDto toDto(ItemRequest request) {
        if (request == null) return null;

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(request.getRequestor())
                .build();
    }

    public ItemRequest toItemRequest(ItemRequestDto requestDto) {
        if (requestDto == null) return null;

        return ItemRequest.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .requestor(requestDto.getRequestor())
                .build();
    }

    public Collection<ItemRequestDto> toDtos(Collection<ItemRequest> requests) {
        return requests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemRequest> toItemRequests(List<ItemRequestDto> dtos) {
        return dtos.stream()
                .map(this::toItemRequest)
                .collect(Collectors.toList());
    }

    public ItemRequest updateItemRequestFromDto(ItemRequestDto requestDto, ItemRequest request) {
        if (requestDto.getDescription() != null) request.setDescription(requestDto.getDescription());
        return request;
    }
}
