package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailExistsException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Save user. Successful save")
    void shouldSaveUserSuccessfully() {
        Long userId = 1L;

        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("New User")
                .email("newuser@yandex.ru")
                .build();

        User user = User.builder()
                .id(userId)
                .name(userCreateDto.getName())
                .email(userCreateDto.getEmail())
                .build();

        UserDto userDto = UserDto.builder()
                .id(userId)
                .name(user.getName())
                .email(user.getEmail())
                .build();

        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(userMapper.toUser(userCreateDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto savedUser = userService.saveUser(userCreateDto);

        assertNotNull(savedUser);
        assertEquals("New User", savedUser.getName());
        assertEquals("newuser@yandex.ru", savedUser.getEmail());
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    @DisplayName("Save user. Email already exists")
    void shouldThrowEmailExistsExceptionWhenEmailAlreadyExists() {
        String existingEmail = "test@yandex.ru";
        User existingUser = User.builder()
                .id(1L)
                .name("Existing User")
                .email(existingEmail)
                .build();

        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("New User")
                .email(existingEmail)
                .build();

        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        EmailExistsException exception = assertThrows(
                EmailExistsException.class,
                () -> userService.saveUser(userCreateDto)
        );

        assertEquals("Email " + existingEmail + " exists already", exception.getMessage());

        verify(userRepository).findAll();
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("Find all users. Successfully found")
    void shouldFindAllUsersSuccessfully() {
        Long userId1 = 1L;
        Long userId2 = 2L;

        User user1 = User.builder()
                .id(userId1)
                .name("User 1")
                .email("user1@yandex.ru")
                .build();

        User user2 = User.builder()
                .id(userId2)
                .name("User 2")
                .email("user2@yandex.ru")
                .build();

        UserDto userDto1 = UserDto.builder()
                .id(userId1)
                .name(user1.getName())
                .email(user1.getEmail())
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(userId2)
                .name(user2.getName())
                .email(user2.getEmail())
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toDtoList(List.of(user1, user2))).thenReturn(List.of(userDto1, userDto2));

        Collection<UserDto> foundUsers = userService.findAll();

        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDtoList(List.of(user1, user2));
    }

    @Test
    @DisplayName("Update user. Successful update")
    void shouldUpdateUserSuccessfully() {
        Long userId = 1L;

        User existingUser = User.builder()
                .id(userId)
                .name("Old Name")
                .email("old@yandex.ru")
                .build();

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Updated Name")
                .email("updated@yandex.ru")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .name("Updated Name")
                .email("updated@yandex.ru")
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(userId)
                .name("Updated Name")
                .email("updated@yandex.ru")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(updatedUserDto);

        UserDto result = userService.updateUser(userId, userUpdateDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@yandex.ru", result.getEmail());
        verify(userRepository, times(1)).save(existingUser);
        verify(userMapper, times(1)).toDto(updatedUser);
    }

    @Test
    @DisplayName("Delete user. Successful delete")
    void shouldDeleteUserSuccessfully() {
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .name("User")
                .email("user@yandex.ru")
                .build();

        UserDto userDto = UserDto.builder()
                .id(userId)
                .name(user.getName())
                .email(user.getEmail())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto deletedUser = userService.deleteUser(userId);

        assertNotNull(deletedUser);
        assertEquals(user.getName(), deletedUser.getName());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Find user by ID. Successfully found")
    void shouldFindUserByIdSuccessfully() {
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .name("User")
                .email("user@yandex.ru")
                .build();

        UserDto userDto = UserDto.builder()
                .id(userId)
                .name("User")
                .email("user@yandex.ru")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto foundUser = userService.findUser(userId);

        assertNotNull(foundUser);
        assertEquals("User", foundUser.getName());
        assertEquals("user@yandex.ru", foundUser.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toDto(user);
    }
}