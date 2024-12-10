package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestExpandedDto;
import ru.practicum.shareit.request.dto.RequestUpdateDto;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@ActiveProfiles("test")
public class RequestServiceImplIntegrationTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        requestRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("Save request. Successful save")
    void shouldSaveRequestSuccessfully() {
        User requestor = userRepository.save(new User(null, "Requestor", "requestor@yandex.ru"));
        RequestCreateDto requestCreateDto = RequestCreateDto.builder()
                .description("Request description")
                .build();

        RequestDto savedRequest = requestService.saveRequest(requestor.getId(), requestCreateDto);

        assertNotNull(savedRequest, "Saved request should not be null");
        assertEquals("Request description", savedRequest.getDescription());
        assertEquals(requestor.getId(), savedRequest.getRequestor().getId());
    }

    @Test
    @DisplayName("Update request. Successful update")
    void shouldUpdateRequestSuccessfully() {
        User requestor = userRepository.save(new User(null, "Requestor", "requestor@yandex.ru"));
        Request request = requestRepository.save(Request.builder()
                .description("Old description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build());

        RequestUpdateDto requestUpdateDto = RequestUpdateDto.builder()
                .id(request.getId())
                .description("Updated description")
                .build();

        RequestDto updatedRequest = requestService.updateRequest(requestor.getId(), requestUpdateDto);

        assertNotNull(updatedRequest);
        assertEquals("Updated description", updatedRequest.getDescription());
    }

    @Test
    @DisplayName("Delete request. Successful delete")
    void shouldDeleteRequestSuccessfully() {
        User requestor = userRepository.save(new User(null, "Requestor", "requestor@yandex.ru"));
        Request request = requestRepository.save(Request.builder()
                .description("Request to be deleted")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build());

        RequestDto deletedRequest = requestService.deleteRequest(requestor.getId(), request.getId());

        assertNotNull(deletedRequest);
        assertEquals(request.getId(), deletedRequest.getId());
        assertTrue(requestRepository.findById(request.getId()).isEmpty());
    }

    @Test
    @DisplayName("Find request by ID. Successfully found")
    void shouldFindRequestByIdSuccessfully() {
        User requestor = userRepository.save(new User(null, "Requestor", "requestor@yandex.ru"));
        Request request = requestRepository.save(Request.builder()
                .description("Request description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(requestor)
                .request(request)
                .build());

        RequestExpandedDto foundRequest = requestService.findRequest(request.getId());

        assertNotNull(foundRequest);
        assertEquals(request.getId(), foundRequest.getId());
        assertEquals("Request description", foundRequest.getDescription());
        assertNotNull(foundRequest.getItems());
        assertEquals(1, foundRequest.getItems().size());
        assertEquals(item.getId(), foundRequest.getItems().get(0).getId());
        assertEquals(item.getName(), foundRequest.getItems().get(0).getName());
    }

    @Test
    @DisplayName("Find all requests by requestor. Successfully found")
    void shouldFindAllRequestsByRequestorSuccessfully() {
        User requestor = userRepository.save(new User(null, "Requestor", "requestor@yandex.ru"));
        requestRepository.save(Request.builder()
                .description("First request")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build());
        requestRepository.save(Request.builder()
                .description("Second request")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build());

        List<RequestExpandedDto> requests = requestService.findAllByRequestor(requestor.getId());

        assertNotNull(requests);
        assertEquals(2, requests.size());
    }

    @Test
    @DisplayName("Find all requests. Successfully found")
    void shouldFindAllRequestsSuccessfully() {
        User requestor1 = userRepository.save(new User(null, "Requestor 1", "requestor1@yandex.ru"));
        User requestor2 = userRepository.save(new User(null, "Requestor 2", "requestor2@yandex.ru"));

        Request request1 = requestRepository.save(Request.builder()
                .description("Request by requestor 1")
                .requestor(requestor1)
                .created(LocalDateTime.now())
                .build());
        Request request2 = requestRepository.save(Request.builder()
                .description("Request by requestor 2")
                .requestor(requestor2)
                .created(LocalDateTime.now())
                .build());

        Item item1 = itemRepository.save(Item.builder()
                .name("Test Item 1")
                .description("Test Description 1")
                .available(true)
                .owner(requestor1)
                .request(request1)
                .build());

        Item item2 = itemRepository.save(Item.builder()
                .name("Test Item 2")
                .description("Test Description 2")
                .available(true)
                .owner(requestor2)
                .request(request2)
                .build());

        Collection<RequestExpandedDto> requests = requestService.findAll(requestor1.getId());

        assertNotNull(requests);
        assertEquals(1, requests.size());

        RequestExpandedDto foundRequest = requests.stream()
                .filter(req -> req.getId().equals(request2.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError());

        assertNotNull(foundRequest.getItems());
        assertEquals(1, foundRequest.getItems().size());
        assertEquals(item2.getId(), foundRequest.getItems().get(0).getId());
        assertEquals(item2.getName(), foundRequest.getItems().get(0).getName());
    }
}