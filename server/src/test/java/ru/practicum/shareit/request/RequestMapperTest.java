package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExpandedDto;
import ru.practicum.shareit.request.dto.RequestUpdateDto;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class RequestMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RequestMapper requestMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Request to DTO")
    void toDto_shouldMapRequestToRequestDto() {
        User requestor = new User(1L, "User", "user@yandex.ru");
        Request request = new Request(2L, "Request description", requestor, LocalDateTime.now());

        when(userMapper.toDto(requestor)).thenReturn(UserDto.builder()
                .id(requestor.getId())
                .name(requestor.getName())
                .email(requestor.getEmail())
                .build());

        RequestDto requestDto = requestMapper.toDto(request);

        assertNotNull(requestDto);
        assertEquals(2L, requestDto.getId());
        assertEquals("Request description", requestDto.getDescription());
        assertEquals(requestor.getId(), requestDto.getRequestor().getId());
        assertEquals(requestor.getName(), requestDto.getRequestor().getName());
    }

    @Test
    @DisplayName("Request to DTO. Null request")
    void toDto_shouldReturnNullWhenRequestIsNull() {
        assertNull(requestMapper.toDto(null));
    }

    @Test
    @DisplayName("Request to Expanded DTO")
    void toExpandedDto_shouldMapRequestToRequestExpandedDto() {
        User requestor = new User(1L, "User", "user@yandex.ru");
        Request request = new Request(2L, "Request description", requestor, LocalDateTime.now());

        ItemDto itemDto1 = ItemDto.builder().id(1L).name("Item 1").build();
        ItemDto itemDto2 = ItemDto.builder().id(2L).name("Item 2").build();
        List<ItemDto> items = List.of(itemDto1, itemDto2);

        when(userMapper.toDto(requestor)).thenReturn(UserDto.builder()
                .id(requestor.getId())
                .name(requestor.getName())
                .email(requestor.getEmail())
                .build());

        RequestExpandedDto requestExpandedDto = requestMapper.toExpandedDto(request, items);

        assertNotNull(requestExpandedDto);
        assertEquals(2L, requestExpandedDto.getId());
        assertEquals("Request description", requestExpandedDto.getDescription());
        assertEquals(requestor.getId(), requestExpandedDto.getRequestor().getId());
        assertEquals(2, requestExpandedDto.getItems().size());
        assertEquals("Item 1", requestExpandedDto.getItems().get(0).getName());
    }

    @Test
    @DisplayName("Request to Expanded DTO. Null request")
    void toExpandedDto_shouldReturnNullWhenRequestIsNull() {
        assertNull(requestMapper.toExpandedDto(null, Collections.emptyList()));
    }

    @Test
    @DisplayName("DTO to Request")
    void toRequest_shouldMapRequestCreateDtoToRequest() {
        User requestor = new User(1L, "User", "user@yandex.ru");

        RequestCreateDto requestCreateDto = RequestCreateDto.builder()
                .description("Request description")
                .build();

        Request request = requestMapper.toRequest(requestCreateDto, requestor);

        assertNotNull(request);
        assertEquals("Request description", request.getDescription());
        assertEquals(requestor, request.getRequestor());
    }

    @Test
    @DisplayName("DTO to Request. Null DTO")
    void toRequest_shouldReturnNullWhenRequestCreateDtoIsNull() {
        assertNull(requestMapper.toRequest(null, new User()));
    }

    @Test
    @DisplayName("Requests to DTOs list")
    void mapRequestsToDtos_shouldMapListOfRequestsToRequestDtoList() {
        User requestor = new User(1L, "User", "user@yandex.ru");
        Request request = new Request(2L, "Request description", requestor, LocalDateTime.now());

        when(userMapper.toDto(requestor)).thenReturn(UserDto.builder()
                .id(requestor.getId())
                .name(requestor.getName())
                .email(requestor.getEmail())
                .build());

        List<RequestDto> requestDtoList = (List<RequestDto>) requestMapper.mapRequestsToDtos(List.of(request));

        assertNotNull(requestDtoList);
        assertEquals(1, requestDtoList.size());
        assertEquals("Request description", requestDtoList.get(0).getDescription());
    }

    @Test
    @DisplayName("Request update from DTO")
    void updateRequestFromDto_shouldUpdateRequestFields() {
        Request request = new Request(2L, "Old description", new User(), LocalDateTime.now());

        RequestUpdateDto requestUpdateDto = RequestUpdateDto.builder()
                .description("New description")
                .build();

        requestMapper.updateRequestFromDto(requestUpdateDto, request);

        assertEquals("New description", request.getDescription());
    }
}