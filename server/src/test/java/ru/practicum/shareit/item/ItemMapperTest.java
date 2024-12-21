package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExpandedDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ItemMapperTest {

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemMapper itemMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Item to DTO")
    void toDto_shouldMapItemToItemDto() {
        User owner = new User(1L, "User", "user@yandex.ru");
        Request request = new Request(2L, "Request description", owner, LocalDateTime.now());
        Item item = Item.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        ItemDto itemDto = itemMapper.toDto(item);

        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Item Name", itemDto.getName());
        assertEquals("Item Description", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(1L, itemDto.getOwnerId());
        assertEquals(2L, itemDto.getRequestId());
    }

    @Test
    @DisplayName("Item to DTO. Null item")
    void toDto_shouldReturnNullWhenItemIsNull() {
        assertNull(itemMapper.toDto(null));
    }

    @Test
    @DisplayName("Item to Expanded DTO")
    void toExpandedDto_shouldMapItemToItemExpandedDto() {
        User owner = new User(1L, "User", "user@yandex.ru");
        Item item = Item.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build();

        Comment comment = new Comment(1L, "Great item!", item, owner, LocalDateTime.now());
        CommentDto commentDto = CommentDto.builder().id(1L).text("Great item!").build();

        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        ItemExpandedDto itemExpandedDto = itemMapper.toExpandedDto(item, List.of(comment), Optional.empty(),
                Optional.empty());

        assertNotNull(itemExpandedDto);
        assertEquals(1L, itemExpandedDto.getId());
        assertEquals("Item Name", itemExpandedDto.getName());
        assertEquals("Item Description", itemExpandedDto.getDescription());
        assertTrue(itemExpandedDto.getAvailable());
        assertEquals(1, itemExpandedDto.getComments().size());
        assertEquals("Great item!", itemExpandedDto.getComments().get(0).getText());
    }

    @Test
    @DisplayName("Item to Expanded DTO. Null item")
    void toExpandedDto_shouldReturnNullWhenItemIsNull() {
        assertNull(itemMapper.toExpandedDto(null, Collections.emptyList(), Optional.empty(),
                Optional.empty()));
    }

    @Test
    @DisplayName("DTO to item")
    void toItem_shouldMapItemCreateDtoToItem() {
        User owner = new User(1L, "User", "user@yandex.ru");
        Request request = new Request(2L, "Request description", owner, LocalDateTime.now());

        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .requestId(2L)
                .build();

        Item item = itemMapper.toItem(itemCreateDto, owner, request);

        assertNotNull(item);
        assertEquals("Item Name", item.getName());
        assertEquals("Item Description", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    @DisplayName("DTO to item. Null DTO")
    void toItem_shouldReturnNullWhenItemCreateDtoIsNull() {
        assertNull(itemMapper.toItem(null, new User(), new Request()));
    }

    @Test
    @DisplayName("Item list to DTO list")
    void toDtoList_shouldMapListOfItemsToItemDtoList() {
        User owner = new User(1L, "User", "user@yandex.ru");
        Item item = Item.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .owner(owner)
                .build();

        List<ItemDto> itemDtoList = itemMapper.toDtoList(List.of(item));

        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
        assertEquals("Item Name", itemDtoList.get(0).getName());
    }

    @Test
    @DisplayName("Item update from DTO")
    void updateItemFromDto_shouldUpdateItemFields() {
        Item item = Item.builder()
                .id(1L)
                .name("Old Name")
                .description("Old Description")
                .available(false)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .name("New Name")
                .description("New Description")
                .available(true)
                .build();

        itemMapper.updateItemFromDto(itemDto, item);

        assertEquals("New Name", item.getName());
        assertEquals("New Description", item.getDescription());
        assertTrue(item.getAvailable());
    }
}