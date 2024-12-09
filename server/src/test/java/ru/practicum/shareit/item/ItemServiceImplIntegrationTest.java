package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;


import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@ActiveProfiles("test")
public class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RequestRepository requestRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
    }


    @Test
    @DisplayName("Save item. Successful save")
    void shouldSaveItemSuccessfully() {
        User user = userRepository.save(new User(null, "User", "user@yandex.ru"));
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("Item Name")
                .description("Description")
                .available(true)
                .build();

        ItemDto savedItem = itemService.saveItem(user.getId(), itemCreateDto);

        assertNotNull(savedItem);
        assertEquals("Item Name", savedItem.getName());
        assertEquals("Description", savedItem.getDescription());
    }

    @Test
    @DisplayName("Update item. Successful update")
    void shouldUpdateItemSuccessfully() {
        User user = userRepository.save(new User(null, "User", "user@yandex.ru"));
        Item item = itemRepository.save(Item.builder()
                .name("Old Name")
                .description("Old Description")
                .available(true)
                .owner(user)
                .build());

        ItemDto itemDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        ItemDto updatedItem = itemService.updateItem(user.getId(), item.getId(), itemDto);

        assertNotNull(updatedItem);
        assertEquals("Updated Name", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    @DisplayName("Delete item. Successful delete")
    void shouldDeleteItemSuccessfully() {
        User user = userRepository.save(new User(null, "User", "user@yandex.ru"));
        Item item = itemRepository.save(Item.builder()
                .name("Item Name")
                .description("Description")
                .available(true)
                .owner(user)
                .build());

        ItemDto deletedItem = itemService.deleteItem(user.getId(), item.getId());

        assertNotNull(deletedItem);
        assertEquals(item.getId(), deletedItem.getId());
        assertTrue(itemRepository.findById(item.getId()).isEmpty());
    }

    @Test
    @DisplayName("Find item by ID. Successfully found")
    void shouldFindItemByIdSuccessfully() {
        User user = userRepository.save(new User(null, "User", "user@yandex.ru"));
        Item item = itemRepository.save(Item.builder()
                .name("Item Name")
                .description("Description")
                .available(true)
                .owner(user)
                .build());

        ItemExpandedDto foundItem = itemService.findItem(item.getId());

        assertNotNull(foundItem);
        assertEquals("Item Name", foundItem.getName());
        assertEquals("Description", foundItem.getDescription());
    }

    @Test
    @DisplayName("Find items by owner. Successfully found")
    void shouldFindItemsByOwnerSuccessfully() {
        User user = userRepository.save(new User(null, "User", "user@yandex.ru"));
        itemRepository.save(Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .owner(user)
                .build());
        itemRepository.save(Item.builder()
                .name("Item 2")
                .description("Description 2")
                .available(false)
                .owner(user)
                .build());

        Collection<ItemExpandedDto> items = itemService.findAllByOwner(user.getId());

        assertNotNull(items);
        assertEquals(2, items.size());
    }

    @Test
    @DisplayName("Search items by text")
    void shouldSearchItemsByText() {
        User user = userRepository.save(new User(null, "User", "user@yandex.ru"));

        Item item1 = itemRepository.save(Item.builder()
                .name("Laptop")
                .description("Good for programming")
                .available(true)
                .owner(user)
                .build());

        Item item2 = itemRepository.save(Item.builder()
                .name("Phone")
                .description("Good for communication")
                .available(true)
                .owner(user)
                .build());

        itemRepository.flush();

        Collection<ItemDto> items = itemService.search("good");

        assertNotNull(items);
        assertEquals(2, items.size());
    }
}