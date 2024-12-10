package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestUpdateDto;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(long userId, RequestCreateDto requestCreateDto) {
        return post("", userId, requestCreateDto);
    }

    public ResponseEntity<Object> updateRequest(long userId, long requestId, RequestUpdateDto requestUpdateDto) {
        return put("/" + requestId, userId, requestUpdateDto);
    }

    public ResponseEntity<Object> deleteRequest(long userId, long requestId) {
        return delete("/" + requestId, userId);
    }

    public ResponseEntity<Object> findRequest(long requestId) {
        return get("/" + requestId);
    }

    public ResponseEntity<Object> findAllRequestsByRequestor(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findAllRequests() {
        return get("/all");
    }
}