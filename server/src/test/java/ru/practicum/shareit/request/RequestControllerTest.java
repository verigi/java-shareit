package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExpandedDto;
import ru.practicum.shareit.request.dto.RequestUpdateDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestService requestService;

    @Test
    @DisplayName("Creating a request")
    void shouldCreateRequest() throws Exception {
        Long requestorId = 1L;

        RequestCreateDto requestCreateDto = RequestCreateDto.builder()
                .description("Need something to test")
                .requestorId(requestorId)
                .build();

        RequestDto requestDto = RequestDto.builder()
                .id(1L)
                .description("Need something to test")
                .requestor(UserDto.builder().id(requestorId).name("User Name").build())
                .created(LocalDateTime.now())
                .build();

        Mockito.when(requestService.saveRequest(eq(requestorId), any(RequestCreateDto.class))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requestorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need something to test"));

        verify(requestService, times(1)).saveRequest(eq(requestorId), any(RequestCreateDto.class));
    }

    @Test
    @DisplayName("Updating a request")
    void shouldUpdateRequest() throws Exception {
        RequestUpdateDto requestUpdateDto = RequestUpdateDto.builder()
                .description("Updated description")
                .build();

        RequestDto requestDto = RequestDto.builder()
                .id(1L)
                .description("Updated description")
                .requestor(UserDto.builder().id(1L).name("User Name").build())
                .created(LocalDateTime.now())
                .build();

        Mockito.when(requestService.updateRequest(anyLong(), any(RequestUpdateDto.class))).thenReturn(requestDto);

        mockMvc.perform(put("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @DisplayName("Deleting a request")
    void shouldDeleteRequest() throws Exception {
        mockMvc.perform(delete("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNoContent());

        verify(requestService, times(1)).deleteRequest(1L, 1L);
    }

    @Test
    @DisplayName("Finding a request by ID")
    void shouldFindRequestById() throws Exception {
        RequestExpandedDto requestDto = RequestExpandedDto.builder()
                .id(1L)
                .description("Need something to test")
                .requestor(UserDto.builder().id(1L).name("User Name").build())
                .created(LocalDateTime.now())
                .items(List.of(ItemDto.builder().id(10L).name("Test Item").build()))
                .build();

        Mockito.when(requestService.findRequest(eq(1L))).thenReturn(requestDto);

        mockMvc.perform(get("/requests/{requestId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need something to test"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].id").value(10L))
                .andExpect(jsonPath("$.items[0].name").value("Test Item"));
    }

    @Test
    @DisplayName("Finding all requests by requestor")
    void shouldFindAllRequestsByRequestor() throws Exception {
        RequestExpandedDto requestExpandedDto = RequestExpandedDto.builder()
                .id(1L)
                .description("Need something to test")
                .requestor(UserDto.builder().id(1L).name("User Name").build())
                .created(LocalDateTime.now())
                .items(List.of(ItemDto.builder().id(1L).name("Something").available(true).build()))
                .build();

        Mockito.when(requestService.findAllByRequestor(eq(1L))).thenReturn(List.of(requestExpandedDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Need something to test"))
                .andExpect(jsonPath("$[0].items[0].name").value("Something"));
    }

    @Test
    @DisplayName("Finding all requests")
    void shouldFindAllRequests() throws Exception {
        RequestExpandedDto requestDto = RequestExpandedDto.builder()
                .id(1L)
                .description("Need something to test")
                .requestor(UserDto.builder().id(1L).name("User Name").build())
                .created(LocalDateTime.now())
                .items(List.of(ItemDto.builder().id(10L).name("Test Item").build()))
                .build();

        Mockito.when(requestService.findAll()).thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Need something to test"))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items[0].id").value(10L))
                .andExpect(jsonPath("$[0].items[0].name").value("Test Item"));
    }
}