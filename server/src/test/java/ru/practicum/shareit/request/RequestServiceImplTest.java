package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NoSuchRequestException;
import ru.practicum.shareit.exception.RequestAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExpandedDto;
import ru.practicum.shareit.request.dto.RequestUpdateDto;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private RequestServiceImpl requestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Save request. Successful save")
    void shouldSaveRequestSuccessfully() {
        Long requestorId = 1L;

        User requestor = new User(requestorId, "Test User", "user@yandex.ru");
        RequestCreateDto requestCreateDto = RequestCreateDto.builder()
                .description("Request description")
                .build();

        Request request = Request.builder()
                .id(2L)
                .description(requestCreateDto.getDescription())
                .requestor(requestor)
                .build();

        RequestDto requestDto = RequestDto.builder()
                .id(2L)
                .description(requestCreateDto.getDescription())
                .requestor(UserDto.builder().id(requestorId).name("Test User").build())
                .build();

        when(userRepository.findById(requestorId)).thenReturn(Optional.of(requestor));
        when(requestMapper.toRequest(requestCreateDto, requestor)).thenReturn(request);
        when(requestRepository.save(any(Request.class))).thenReturn(request);
        when(requestMapper.toDto(request)).thenReturn(requestDto);

        RequestDto savedRequest = requestService.saveRequest(requestorId, requestCreateDto);

        assertNotNull(savedRequest);
        assertEquals(requestDto.getId(), savedRequest.getId());
        assertEquals(requestCreateDto.getDescription(), savedRequest.getDescription());
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    @DisplayName("Update request. Successful update")
    void shouldUpdateRequestSuccessfully() {
        Long requestId = 2L;
        Long requestorId = 1L;

        User requestor = User.builder()
                .id(requestorId)
                .name("Requestor")
                .email("requestor@yandex.ru")
                .build();

        Request existingRequest = Request.builder()
                .id(requestId)
                .description("Request description")
                .requestor(requestor)
                .build();

        RequestUpdateDto requestUpdateDto = RequestUpdateDto.builder()
                .id(requestId)
                .description("Updated description")
                .build();

        Request updatedRequest = Request.builder()
                .id(requestId)
                .description("Updated description")
                .requestor(requestor)
                .build();

        RequestDto updatedRequestDto = RequestDto.builder()
                .id(requestId)
                .description("Updated description")
                .requestor(userMapper.toDto(requestor))
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));
        when(requestRepository.save(any(Request.class))).thenReturn(updatedRequest);
        when(requestMapper.toDto(any(Request.class))).thenReturn(updatedRequestDto);

        RequestDto result = requestService.updateRequest(requestorId, requestUpdateDto);

        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
        verify(requestRepository, times(1)).save(any(Request.class));
        verify(requestMapper, times(1)).toDto(any(Request.class));
    }

    @DisplayName("Update request. Request is not found")
    @Test
    void shouldThrowNoSuchRequestExceptionWhenRequestNotFound() {
        Long requestorId = 1L;
        Long requestId = 2L;

        RequestUpdateDto requestUpdateDto = RequestUpdateDto.builder()
                .id(requestId)
                .description("Updated description")
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        NoSuchRequestException exception = assertThrows(
                NoSuchRequestException.class,
                () -> requestService.updateRequest(requestorId, requestUpdateDto)
        );

        assertEquals("Incorrect request id: " + requestId, exception.getMessage());

        verify(requestRepository).findById(requestId);

        verifyNoInteractions(requestMapper);
    }

    @Test
    @DisplayName("Update request. User has no permissions")
    void shouldThrowRequestAccessExceptionWhenUserHasNoPermissions() {
        Long requestorId = 1L;
        Long actualRequestorId = 2L;
        Long requestId = 3L;

        RequestUpdateDto requestUpdateDto = RequestUpdateDto.builder()
                .id(requestId)
                .description("Updated description")
                .build();

        Request existingRequest = Request.builder()
                .id(requestId)
                .description("Original description")
                .requestor(User.builder().id(actualRequestorId).build())
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(existingRequest));

        RequestAccessException exception = assertThrows(
                RequestAccessException.class,
                () -> requestService.updateRequest(requestorId, requestUpdateDto)
        );

        assertEquals("Not enough permissions: user with ID " + requestorId + " is not requestor",
                exception.getMessage());

        verify(requestRepository).findById(requestId);

        verifyNoInteractions(requestMapper);
    }

    @Test
    @DisplayName("Delete request. Successful delete")
    void shouldDeleteRequestSuccessfully() {
        Long requestorId = 1L;
        Long requestId = 2L;

        User requestor = new User(requestorId, "Requestor", "requestor@yandex.ru");

        Request request = Request.builder()
                .id(requestId)
                .description("Request description")
                .requestor(requestor)
                .build();

        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(requestMapper.toDto(request)).thenReturn(RequestDto.builder().id(requestId).description("Request description").build());

        RequestDto deletedRequest = requestService.deleteRequest(requestorId, requestId);

        assertNotNull(deletedRequest);
        assertEquals(requestId, deletedRequest.getId());
        verify(requestRepository).delete(request);
    }

    @Test
    @DisplayName("Find request by ID. Successfully found")
    void shouldFindRequestByIdSuccessfully() {
        Long requestId = 2L;

        User requestor = new User(1L, "Requestor", "requestor@yandex.ru");
        Request request = Request.builder()
                .id(requestId)
                .description("Request description")
                .requestor(requestor)
                .build();

        List<Item> items = List.of(Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .request(request)
                .build());

        List<ItemDto> itemDtos = List.of(ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build());

        RequestExpandedDto requestExpandedDto = RequestExpandedDto.builder()
                .id(requestId)
                .description("Request description")
                .requestor(UserDto.builder().id(1L).name("Requestor").build())
                .created(LocalDateTime.now())
                .items(itemDtos)
                .build();

        when(requestRepository.findById(eq(requestId))).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(eq(requestId))).thenReturn(items);
        when(itemMapper.toDtoList(items)).thenReturn(itemDtos);
        when(requestMapper.toExpandedDto(request, itemDtos)).thenReturn(requestExpandedDto);

        RequestExpandedDto foundRequest = requestService.findRequest(requestId);

        assertNotNull(foundRequest);
        assertEquals(requestId, foundRequest.getId());
        assertEquals("Request description", foundRequest.getDescription());
        assertNotNull(foundRequest.getItems());
        assertEquals(1, foundRequest.getItems().size());
        assertEquals(itemDtos.get(0).getId(), foundRequest.getItems().get(0).getId());

        verify(requestRepository).findById(requestId);
        verify(itemRepository).findByRequestId(eq(requestId));
        verify(itemMapper).toDtoList(items);
        verify(requestMapper).toExpandedDto(request, itemDtos);
    }

    @Test
    @DisplayName("Find all requests. Successfully found")
    void shouldFindAllRequestsSuccessfully() {
        Long currentUserId = 1L;
        User requestor = new User(2L, "Requestor", "requestor@yandex.ru");

        Request request = Request.builder()
                .id(2L)
                .description("Request description")
                .requestor(requestor)
                .build();

        List<Item> items = List.of(Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .request(request)
                .build());

        List<ItemDto> itemDtos = List.of(ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build());

        RequestExpandedDto requestExpandedDto = RequestExpandedDto.builder()
                .id(2L)
                .description("Request description")
                .requestor(UserDto.builder().id(2L).name("Requestor").build())
                .created(LocalDateTime.now())
                .items(itemDtos)
                .build();

        when(requestRepository.findAllByRequestorIdNot(currentUserId)).thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(List.of(2L))).thenReturn(items);
        when(itemMapper.toDtoList(items)).thenReturn(itemDtos);
        when(requestMapper.toExpandedDto(request, itemDtos)).thenReturn(requestExpandedDto);

        Collection<RequestExpandedDto> requests = requestService.findAll(currentUserId);

        assertNotNull(requests);
        assertEquals(1, requests.size());

        RequestExpandedDto actualRequest = requests.iterator().next();
        assertNotNull(actualRequest);
        assertEquals(2L, actualRequest.getId());
        assertEquals("Request description", actualRequest.getDescription());
        assertNotNull(actualRequest.getItems());
        assertEquals(1, actualRequest.getItems().size());
        assertEquals(1L, actualRequest.getItems().get(0).getId());
        assertEquals("Item 1", actualRequest.getItems().get(0).getName());

        verify(requestRepository).findAllByRequestorIdNot(currentUserId);
        verify(itemRepository).findByRequestIdIn(List.of(2L));
        verify(requestMapper).toExpandedDto(request, itemDtos);
    }

    @Test
    @DisplayName("Find all requests by requestor. Successfully found")
    void shouldFindAllRequestsByRequestorSuccessfully() {
        Long requestorId = 1L;

        User requestor = new User(requestorId, "Requestor", "requestor@yandex.ru");
        Request request = Request.builder()
                .id(2L)
                .description("Request description")
                .requestor(requestor)
                .build();

        Item item = Item.builder()
                .id(3L)
                .name("Item")
                .available(true)
                .request(request)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(3L)
                .name("Item")
                .available(true)
                .build();

        RequestExpandedDto requestExpandedDto = RequestExpandedDto.builder()
                .id(2L)
                .description("Request description")
                .items(List.of(itemDto))
                .build();

        when(userRepository.findById(requestorId)).thenReturn(Optional.of(requestor));
        when(requestRepository.findAllByRequestorId(requestorId)).thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(List.of(2L))).thenReturn(List.of(item));
        when(itemMapper.toDtoList(List.of(item))).thenReturn(List.of(itemDto));
        when(requestMapper.toExpandedDto(request, List.of(itemDto))).thenReturn(requestExpandedDto);

        List<RequestExpandedDto> foundRequests = requestService.findAllByRequestor(requestorId);

        assertNotNull(foundRequests);
        assertEquals(1, foundRequests.size());
        assertEquals("Request description", foundRequests.get(0).getDescription());
        verify(requestRepository).findAllByRequestorId(requestorId);
    }
}