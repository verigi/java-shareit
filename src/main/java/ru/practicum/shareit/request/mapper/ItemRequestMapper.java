package ru.practicum.shareit.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {
    public ItemRequestDto toDto(ItemRequest request) {
        return request == null ? null : ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(request.getRequestor())
                .created(request.getCreated())
                .build();
    }

    public ItemRequest toEntity(ItemRequest request) {
        return request == null ? null : ItemRequest.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(request.getRequestor())
                .created(request.getCreated())
                .build();
    }

    public ItemRequest toItemRequest(ItemRequest requestEntity) {
        return requestEntity == null ? null : ItemRequest.builder()
                .id(requestEntity.getId())
                .description(requestEntity.getDescription())
                .requestor(requestEntity.getRequestor())
                .created(requestEntity.getCreated())
                .build();
    }

    public ItemRequest toItemRequest(ItemRequestDto requestDto) {
        return requestDto == null ? null : ItemRequest.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .requestor(requestDto.getRequestor())
                .created(requestDto.getCreated())
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