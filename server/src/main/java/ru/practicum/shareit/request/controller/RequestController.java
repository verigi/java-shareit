package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExpandedDto;
import ru.practicum.shareit.request.dto.RequestUpdateDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestService service;

    @Autowired
    public RequestController(RequestService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RequestDto> create(
            @RequestHeader("X-Sharer-User-Id") Long requestorId,
            @Valid @RequestBody RequestCreateDto request
    ) {
        log.info("Received POST request to create request");
        RequestDto createdRequest = service.saveRequest(requestorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    @GetMapping
    public ResponseEntity<Collection<RequestExpandedDto>> findAllRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Received GET request. Find all requests by requestor");
        Collection<RequestExpandedDto> requests = service.findAllByRequestor(requestorId);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<RequestDto> update(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId,
            @Valid @RequestBody RequestUpdateDto newRequest) {
        log.info("Received PUT request to update request with ID: {}", requestId);
        newRequest.setId(requestId);
        RequestDto updatedRequest = service.updateRequest(userId, newRequest);
        return ResponseEntity.ok(updatedRequest);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable Long requestId) {
        log.info("Received DELETE request, request with ID: {}", requestId);
        service.deleteRequest(userId, requestId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestExpandedDto> find(@PathVariable Long requestId) {
        log.info("Received GET request. Find request by id");
        RequestExpandedDto request = service.findRequest(requestId);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<RequestExpandedDto>> findAllRequests() {
        log.info("Received GET request. Find all requests");
        Collection<RequestExpandedDto> requests = service.findAll();
        return ResponseEntity.ok(requests);
    }
}