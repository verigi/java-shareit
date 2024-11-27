package ru.practicum.shareit.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;

@Component
public class BookingMapper {
    ItemMapper itemMapper;
    UserMapper userMapper;

    @Autowired
    public BookingMapper(ItemMapper itemMapper, UserMapper userMapper) {
        this.itemMapper = itemMapper;
        this.userMapper = userMapper;
    }

    public BookingDto toDto(Booking booking) {
        return booking == null ? null : BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemMapper.toDto(booking.getItem()))
                .booker(userMapper.toDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public Booking toBooking(BookingCreateDto bookingDto, Item item, User booker) {
        return bookingDto == null ? null : ru.practicum.shareit.booking.entity.Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }

    public Booking updateBookingFromDto(BookingUpdateDto bookingDto, Booking booking) {
        if (bookingDto.getStart() != null) booking.setStart(bookingDto.getStart());
        if (bookingDto.getEnd() != null) booking.setEnd(bookingDto.getEnd());

        return booking;
    }
}