package ru.practicum.shareit.request.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExpandedDto;
import ru.practicum.shareit.request.dto.RequestUpdateDto;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {
    private UserMapper userMapper;
    private ItemMapper itemMapper;

    @Autowired
    public RequestMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public RequestDto toDto(Request request) {
        return request == null ? null : RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(userMapper.toDto(request.getRequestor()))
                .created(request.getCreated())
                .build();
    }

    public RequestExpandedDto toExpandedDto(Request request, List<ItemDto> items) {
        return request == null ? null : RequestExpandedDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requestor(userMapper.toDto(request.getRequestor()))
                .created(request.getCreated())
                .items(items)
                .build();
    }

    public Request toRequest(RequestCreateDto requestDto, User requestor) {
        return requestDto == null ? null : Request.builder()
                .description(requestDto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    public Collection<RequestDto> mapRequestsToDtos(Collection<Request> requests) {
        return requests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Request updateRequestFromDto(RequestUpdateDto requestDto, Request request) {
        if (requestDto.getDescription() != null) request.setDescription(requestDto.getDescription());

        return request;
    }
}