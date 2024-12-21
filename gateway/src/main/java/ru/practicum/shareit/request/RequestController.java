package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestUpdateDto;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody RequestCreateDto requestCreateDto) {
        log.info("Creating request, userId={}", userId);
        return requestClient.createRequest(userId, requestCreateDto);
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<Object> updateRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId,
            @Valid @RequestBody RequestUpdateDto requestUpdateDto) {
        log.info("Updating request {}, userId={}", requestId, userId);
        return requestClient.updateRequest(userId, requestId, requestUpdateDto);
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<Object> deleteRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long requestId) {
        log.info("Deleting request {}, userId={}", requestId, userId);
        return requestClient.deleteRequest(userId, requestId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequest(@PathVariable long requestId) {
        log.info("Finding request with id={}", requestId);
        return requestClient.findRequest(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllRequestsByRequestor(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Finding all requests for userId={}", userId);
        return requestClient.findAllRequestsByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequests() {
        log.info("Finding all requests");
        return requestClient.findAllRequests();
    }
}