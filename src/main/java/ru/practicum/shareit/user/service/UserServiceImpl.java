package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.CommonChecker;
import ru.practicum.shareit.exception.EmailExistsException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
public class UserServiceImpl extends CommonChecker implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UserDto saveUser(UserCreateDto userCreateDto) {
        log.debug("Save user request received. User name: {}", userCreateDto.getName());

        if (isEmailExists(userCreateDto.getEmail())) {
            log.warn("Email exists already");
            throw new EmailExistsException("Email " + userCreateDto.getEmail() + " exists already");
        }
        User user = userRepository.save(mapper.toUser(userCreateDto));

        return mapper.toDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        log.debug("Update user request received. User id: {}", userId);

        User existingUser = checkUserAndReturn(userId);

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
        return mapper.toDto(updatedUser);
    }

    @Override
    public UserDto deleteUser(Long userId) {
        log.debug("Delete user request received. User id: {}", userId);
        User user = checkUserAndReturn(userId);

        log.debug("Deleting successful!");
        userRepository.delete(user);

        return mapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findUser(Long userId) {
        log.debug("Get user request received. User id: {}", userId);

        return mapper.toDto(checkUserAndReturn(userId));
    }

    @Override
    public Collection<UserDto> findAll() {
        log.debug("Get all users request received");

        return mapper.toDtoList(new ArrayList<>(userRepository.findAll()));
    }

    private Boolean isEmailExists(String email) {
        return userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}