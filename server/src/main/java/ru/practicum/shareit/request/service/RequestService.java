package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExpandedDto;
import ru.practicum.shareit.request.dto.RequestUpdateDto;

import java.util.Collection;
import java.util.List;

public interface RequestService {
    RequestDto saveRequest(Long requestorId, RequestCreateDto requestDto);

    RequestDto updateRequest(Long requestorId, RequestUpdateDto requestDto);

    RequestDto deleteRequest(Long requestorId, Long requestId);

    RequestExpandedDto findRequest(Long requestId);

    Collection<RequestExpandedDto> findAll();

    List<RequestExpandedDto> findAllByRequestor(Long requestorId);
}