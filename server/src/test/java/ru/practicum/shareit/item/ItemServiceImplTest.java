package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentIncorrectTimeException;
import ru.practicum.shareit.exception.ItemAccessException;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.entity.Comment;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExpandedDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Save item. Successful save")
    void shouldSaveItemSuccessfully() {
        Long ownerId = 1L;
        Long itemId = 1L;

        User user = User.builder().id(ownerId).name("Test user").email("example@yandex.ru").build();
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        Item item = Item.builder()
                .id(itemId)
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .owner(user)
                .build();

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(itemMapper.toItem(any(ItemCreateDto.class), any(User.class), any())).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toDto(any(Item.class))).thenReturn(ItemDto.builder()
                .id(itemId)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(ownerId)
                .build());

        ItemDto result = itemService.saveItem(ownerId, itemCreateDto);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals(itemCreateDto.getName(), result.getName());
        verify(userRepository, times(1)).findById(ownerId);
        verify(itemRepository, times(1)).save(item);
    }


    @Test
    @DisplayName("Update item. Successful update")
    void shouldUpdateItemSuccessfully() {
        User owner = new User(1L, "Owner", "owner@yandex.ru");
        Item item = Item.builder()
                .id(1L)
                .name("Old Name")
                .description("Old Description")
                .available(true)
                .owner(owner)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .name("New Name")
                .description("New Description")
                .available(false)
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.updateItemFromDto(eq(itemDto), any(Item.class))).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

        ItemDto updatedItem = itemService.updateItem(1L, 1L, itemDto);

        assertNotNull(updatedItem);
        assertEquals("New Name", updatedItem.getName());
        assertEquals("New Description", updatedItem.getDescription());
        verify(itemRepository).save(item);
    }

    @Test
    @DisplayName("Update item. Unauthorized update")
    void shouldThrowItemAccessExceptionWhenUnauthorizedUpdate() {
        User owner = new User(1L, "Owner", "owner@yandex.ru");
        User otherUser = new User(2L, "Other User", "other@yandex.ru");
        Item item = Item.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .owner(owner)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemAccessException thrown = assertThrows(ItemAccessException.class,
                () -> itemService.updateItem(2L, 1L, itemDto));

        assertEquals("Only the owner can make updating", thrown.getMessage());
    }

    @Test
    @DisplayName("Delete item. Successful delete")
    void shouldDeleteItemSuccessfully() {
        User owner = new User(1L, "Owner", "owner@yandex.ru");
        Item item = Item.builder()
                .id(1L)
                .name("Item Name")
                .owner(owner)
                .build();
        ItemDto itemDto = ItemDto.builder()
                .name("Item Name")
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

        ItemDto deletedItem = itemService.deleteItem(1L, 1L);

        assertNotNull(deletedItem);
        assertEquals("Item Name", deletedItem.getName());
        verify(itemRepository).delete(item);
    }

    @Test
    @DisplayName("Find item by ID. Successfully found")
    void shouldFindItemByIdSuccessfully() {
        Item item = Item.builder().id(1L).name("Item Name").description("Item Description").available(true).build();
        ItemExpandedDto itemExpandedDto = ItemExpandedDto.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toExpandedDto(eq(item), anyList())).thenReturn(itemExpandedDto);

        ItemExpandedDto foundItem = itemService.findItem(1L);

        assertNotNull(foundItem);
        assertEquals("Item Name", foundItem.getName());
        verify(itemRepository).findById(1L);
    }

    @Test
    @DisplayName("Search items by text")
    void shouldSearchItemsByTextSuccessfully() {
        String searchText = "Item";
        Item item = Item.builder()
                .id(1L)
                .name("Item Name")
                .available(true)
                .build();
        ItemDto itemDto = ItemDto.builder().id(1L).name("Item Name").available(true).build();

        when(itemRepository.search(searchText)).thenReturn(List.of(item));
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

        Collection<ItemDto> foundItems = itemService.search(searchText);

        assertNotNull(foundItems);
        assertEquals(1, foundItems.size());
        assertEquals("Item Name", foundItems.iterator().next().getName());
        verify(itemRepository).search(searchText);
    }

    @Test
    @DisplayName("Add comment. Successful add")
    void shouldAddCommentSuccessfully() {
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Item Name")
                .owner(user)
                .available(true)
                .build();

        Comment comment = Comment.builder()
                .id(1L).text("Great item!")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("Great item!")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentMapper.toComment(any(CommentCreateDto.class), eq(item), eq(user))).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);
        when(bookingRepository.findByItemIdAndBookerIdAndEndBefore(eq(1L), eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(mock(Booking.class)));

        CommentDto addedComment = itemService.addComment(1L, 1L, commentCreateDto);

        assertNotNull(addedComment);
        assertEquals("Great item!", addedComment.getText());
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("Add comment. No completed booking")
    void shouldThrowCommentIncorrectTimeExceptionWhenNoCompletedBooking() {
        User user = new User(1L, "User", "user@yandex.ru");
        Item item = Item.builder()
                .id(1L)
                .name("Item Name")
                .owner(user)
                .available(true)
                .build();

        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .text("Great item!")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndBookerIdAndEndBefore(eq(1L), eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        CommentIncorrectTimeException thrown = assertThrows(CommentIncorrectTimeException.class,
                () -> itemService.addComment(1L, 1L, commentCreateDto));

        assertEquals("You can only comment on items you have completed bookings for.", thrown.getMessage());
    }
}