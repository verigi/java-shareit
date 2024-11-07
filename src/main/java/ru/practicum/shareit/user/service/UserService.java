package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

public interface UserService {
    UserDto save(UserDto userDto);

    UserDto update(Long userId, UserUpdateDto userUpdateDto);

    UserDto delete(Long userId);

    UserDto find(Long userId);

    Collection<UserDto> findAll();
}
