package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailExistsException;
import ru.practicum.shareit.exception.NoSuchUserException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDto saveUser(UserCreateDto userCreateDto) {
        log.debug("Save user request received. User name: {}", userCreateDto.getName());

        if (isEmailExists(userCreateDto.getEmail())) {
            log.warn("Email exists already");
            throw new EmailExistsException("Email " + userCreateDto.getEmail() + " exists already");
        }
        User user = userRepository.save(userMapper.toUser(userCreateDto));

        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        log.debug("Update user request received. User id: {}", userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User not found with id: " + userId));


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

        User updatedUser = userRepository.save(existingUser);
        log.debug("Updating successful!");
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDto deleteUser(Long userId) {
        log.debug("Delete user request received. User id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User not found with id: " + userId));


        log.debug("Deleting successful!");
        userRepository.delete(user);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findUser(Long userId) {
        log.debug("Get user request received. User id: {}", userId);

        return userMapper.toDto(userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User not found with id: " + userId)));
    }

    @Override
    public Collection<UserDto> findAll() {
        log.debug("Get all users request received");

        return userMapper.toDtoList(new ArrayList<>(userRepository.findAll()));
    }

    private Boolean isEmailExists(String email) {
        return userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}