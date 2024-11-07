package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(UserStorage userStorage, UserMapper mapper) {
        this.userStorage = userStorage;
        this.mapper = mapper;
    }

    @Override
    public UserDto save(UserDto userDto) {
        log.debug("Save user request received. User name: {}", userDto.getName());

        User user = mapper.toUser(userDto);
        if (isEmailExists(userDto.getEmail())) {
            log.warn("Email exists already");
            throw new EmailExistsException("Email " + userDto.getEmail() + " exists already");
        }

        log.debug("Saving successful!");
        return mapper.toDto(userStorage.save(user));
    }

    @Override
    public UserDto update(Long userId, UserUpdateDto userUpdateDto) {
        log.debug("Update user request received. User id: {}", userId);

        User existingUser = userStorage.find(userId);

        if (userUpdateDto.getName() != null) {
            existingUser.setName(userUpdateDto.getName());
        }

        if (userUpdateDto.getEmail() != null && !userUpdateDto.getEmail().equals(existingUser.getEmail())) {
            if (isEmailExists(userUpdateDto.getEmail())) {
                log.debug("Email {} exists", userUpdateDto.getEmail());
                throw new EmailExistsException("Email " + userUpdateDto.getEmail() + " registered already");
            }
            existingUser.setEmail(userUpdateDto.getEmail());
        }

        User updatedUser = userStorage.update(existingUser);
        log.debug("Updating successful!");
        return mapper.toDto(updatedUser);
    }

    @Override
    public UserDto delete(Long userId) {
        log.debug("Delete user request received. User id: {}", userId);
        User user = userStorage.find(userId);
        log.debug("Deleting successful!");
        return mapper.toDto(userStorage.delete(userId));
    }

    @Override
    public UserDto find(Long userId) {
        log.debug("Get user request received. User id: {}", userId);
        return mapper.toDto(userStorage.find(userId));
    }

    @Override
    public Collection<UserDto> findAll() {
        log.debug("Get all users request received");
        return mapper.toDtoList(new ArrayList<>(userStorage.findAll()));
    }

    private Boolean isEmailExists(String email) {
        return userStorage.findAll().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}