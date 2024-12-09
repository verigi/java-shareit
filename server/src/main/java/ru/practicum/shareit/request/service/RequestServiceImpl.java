package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.CommonChecker;
import ru.practicum.shareit.exception.RequestAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExpandedDto;
import ru.practicum.shareit.request.dto.RequestUpdateDto;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequestServiceImpl extends CommonChecker implements RequestService {

    private RequestRepository requestRepository;
    private RequestMapper requestMapper;
    private ItemMapper itemMapper;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository,
                              RequestMapper requestMapper, ItemMapper itemMapper) {
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
        this.itemMapper = itemMapper;
    }


    @Override
    @Transactional
    public RequestDto saveRequest(Long requestorId, RequestCreateDto requestDto) {
        log.debug("Save request query received. Requestor id: {}", requestorId);

        User user = checkUserAndReturn(requestorId);
        Request request = requestMapper.toRequest(requestDto, user);

        requestRepository.save(request);

        log.debug("Saving successful!");
        return requestMapper.toDto(request);
    }

    @Override
    @Transactional
    public RequestDto updateRequest(Long requestorId, RequestUpdateDto requestDto) {
        log.debug("Update request query received. Request id: {}", requestDto.getId());

        Request updRequest = checkRequestAndReturn(requestDto.getId());
        validatePermissions(requestorId, updRequest);
        requestMapper.updateRequestFromDto(requestDto, updRequest);

        requestRepository.save(updRequest);
        log.debug("Updating successful! Request id: {}", updRequest.getId());

        return requestMapper.toDto(updRequest);
    }

    @Override
    @Transactional
    public RequestDto deleteRequest(Long requestorId, Long requestId) {
        log.debug("Delete request query received. Request id: {}", requestId);

        Request request = checkRequestAndReturn(requestId);
        validatePermissions(requestorId, request);

        requestRepository.delete(request);
        log.debug("Deleting successful!");

        return requestMapper.toDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public RequestExpandedDto findRequest(Long requestId) {
        log.debug("Get request query received. Request id: {}", requestId);

        Request request = checkRequestAndReturn(requestId);

        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemDto> itemDtos = itemMapper.toDtoList(items);

        return requestMapper.toExpandedDto(request, itemDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestExpandedDto> findAll() {
        log.debug("Get all requests query received");

        List<Request> requests = requestRepository.findAll();

        return fillRequestsWithItems(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestExpandedDto> findAllByRequestor(Long requestorId) {
        log.debug("Get all requests by requestor. Requestor id: {}", requestorId);

        User user = checkUserAndReturn(requestorId);
        List<Request> requests = requestRepository.findAllByRequestorId(requestorId);

        return fillRequestsWithItems(requests);
    }

    private void validatePermissions(Long userId, Request request) {
        if (!request.getRequestor().getId().equals(userId)) {
            throw new RequestAccessException("Not enough permissions: user with ID " + userId + " is not requestor");
        }
    }

    private List<RequestExpandedDto> fillRequestsWithItems(List<Request> requests) {
        log.debug("Collecting request id list");
        List<Long> ids = requests.stream()
                .map(Request::getId)
                .collect(Collectors.toList());

        log.debug("Getting items for each request");
        Map<Long, List<Item>> requestedItems = itemRepository
                .findByRequestIdIn(ids).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream().map(request -> {
            List<Item> items = requestedItems.getOrDefault(request.getId(), Collections.emptyList());
            List<ItemDto> itemDtos = itemMapper.toDtoList(items);
            return requestMapper.toExpandedDto(request, itemDtos);
        }).collect(Collectors.toList());
    }
}