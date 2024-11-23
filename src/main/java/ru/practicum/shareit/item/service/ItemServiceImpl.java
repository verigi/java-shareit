package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentIncorrectTimeException;
import ru.practicum.shareit.exception.ItemOwnerException;
import ru.practicum.shareit.exception.NoSuchItemException;
import ru.practicum.shareit.exception.NoSuchUserException;
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
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemMapper itemMapper;
    private CommentMapper commentMapper;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           ItemMapper itemMapper,
                           CommentMapper commentMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
    }


    @Override
    @Transactional
    public ItemDto saveItem(Long ownerId, ItemCreateDto itemCreateDto) {
        log.debug("Save item request received. Item name: {}, owner id: {}", itemCreateDto.getName(), ownerId);

        User user = checkUserAndReturn(ownerId);
        Item item = itemMapper.toItem(itemCreateDto, user);
        itemRepository.save(item);

        log.debug("Saving successful!");
        return itemMapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        log.debug("Update item request received. Item id: {}, owner id: {}", itemDto.getId(), ownerId);

        Item item = checkItemAndReturn(itemId);
        if (!item.getOwner().getId().equals(ownerId)) {
            log.error("Illegal update request");
            throw new ItemOwnerException("Only the owner can make updating");
        }

        Item updItem = itemMapper.updateItemFromDto(itemDto, item);
        itemRepository.save(updItem);

        log.debug("Updating successful!");
        return itemMapper.toDto(updItem);
    }

    @Override
    @Transactional
    public ItemDto deleteItem(Long ownerId, Long itemId) {
        log.debug("Delete item request received. Item id: {}, owner id: {}", itemId, ownerId);
        Item item = checkItemAndReturn(itemId);

        if (!item.getOwner().getId().equals(ownerId)) {
            log.warn("Illegal deleting request");
            throw new ItemOwnerException("Only the owner can delete the item");
        }
        itemRepository.delete(item);

        log.debug("Deleting successful!");
        return itemMapper.toDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemExpandedDto findItem(Long itemId) {
        log.debug("Get item request received. Item id: {}", itemId);

        Item item = checkItemAndReturn(itemId);
        return itemMapper.toExpandedDto(item,
                commentRepository.findAllByItemId(itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemExpandedDto> findAllByOwner(Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        return fillItemWithData(items);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> search(String text) {
        log.debug("Search item by name/description request received");

        if (text == null || text.trim().isEmpty()) {
            log.debug("No criteria for search provided");
            return Collections.emptyList();
        }

        return itemRepository.search(text).stream()
                .map(itemMapper::toDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentCreateDto commentCreateDto) {
        log.debug("Add comment request received. Item id: {}, author id: {}", itemId, userId);

        Item item = checkItemAndReturn(itemId);
        User user = checkUserAndReturn(userId);

        boolean hasCompletedBooking = bookingRepository.findByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now())
                .isPresent();


        if (!hasCompletedBooking) {
            log.warn("User {} cannot comment on item {} without a completed booking", userId, itemId);
            throw new CommentIncorrectTimeException("You can only comment on items you have completed bookings for.");
        }

        Comment comment = commentMapper.toComment(commentCreateDto, item, user);
        commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    private User checkUserAndReturn(Long userId) {
        log.debug("Checking user");

        return userRepository.findById(userId).orElseThrow(() -> new
                NoSuchUserException("Incorrect user id: " + userId));
    }

    private Item checkItemAndReturn(Long itemId) {
        log.debug("Checking item");

        return itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchItemException("Incorrect item id: " + itemId));
    }

    private List<ItemExpandedDto> fillItemWithData(List<Item> userItems) {
        log.debug("Collecting item id list");
        List<Long> ids = userItems.stream()
                .map(Item::getId)
                .collect(toList());

        log.debug("Getting last ended bookings");
        Map<Item, LocalDateTime> lastItemBookingEndDate = bookingRepository
                .findByItemInAndEndBefore(ids).stream()
                .collect(Collectors.toMap(Booking::getItem, Booking::getEnd));

        log.debug("Getting next closest bookings");
        Map<Item, LocalDateTime> nextItemBookingStartDate = bookingRepository
                .findByItemInAndStartAfter(ids).stream()
                .collect(Collectors.toMap(Booking::getItem, Booking::getStart));

        log.debug("Getting comments for items");
        Map<Item, List<Comment>> itemsWithComments = commentRepository
                .findAllCommentsByItemIn(ids).stream()
                .collect(groupingBy(Comment::getItem, toList()));

        return userItems.stream()
                .map(item -> {
                    Optional<LocalDateTime> lastEndDate = Optional.ofNullable(lastItemBookingEndDate.get(item));
                    Optional<LocalDateTime> nextStartDate = Optional.ofNullable(nextItemBookingStartDate.get(item));
                    List<Comment> comments = itemsWithComments.getOrDefault(item, Collections.emptyList());
                    return itemMapper.toExpandedDto(item, comments, lastEndDate, nextStartDate);
                })
                .collect(toList());
    }


    private Optional<LocalDateTime> getLastBookingEnd(Long itemId) {
        log.debug("Getting last booking end datetime");

        return bookingRepository.findLastBookingEndByItemId(itemId)
                .stream()
                .max(Comparator.naturalOrder());
    }

    private Optional<LocalDateTime> getNextBookingStart(Long itemId) {
        log.debug("Getting next booking start datetime");

        return bookingRepository.findNextBookingStartByItemId(itemId)
                .stream()
                .min(Comparator.naturalOrder());
    }
}