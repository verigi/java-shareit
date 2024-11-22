package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

public interface UserService {
    UserDto saveUser(UserCreateDto userDto);

    UserDto updateUser(Long userId, UserUpdateDto userUpdateDto);

    UserDto deleteUser(Long userId);

    UserDto findUser(Long userId);

    Collection<UserDto> findAll();
}