package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
@ActiveProfiles("test")
public class UserServiceImplIntegrationTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Save user. Successful save")
    void shouldSaveUserSuccessfully() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("Test User")
                .email("test.user@yandex.ru")
                .build();

        UserDto savedUser = userService.saveUser(userCreateDto);

        assertNotNull(savedUser);
        assertEquals(userCreateDto.getName(), savedUser.getName());
        assertEquals(userCreateDto.getEmail(), savedUser.getEmail());

        Optional<User> userFromDb = userRepository.findById(savedUser.getId());
        assertTrue(userFromDb.isPresent());
        assertEquals(userCreateDto.getEmail(), userFromDb.get().getEmail());
    }

    @Test
    @DisplayName("Update user. Successful update")
    void shouldUpdateUserSuccessfully() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("Old Name")
                .email("old.email@yandex.ru")
                .build();

        UserDto savedUser = userService.saveUser(userCreateDto);

        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Updated Name")
                .email("updated.email@yandex.ru")
                .build();

        UserDto updatedUser = userService.updateUser(savedUser.getId(), userUpdateDto);

        assertNotNull(updatedUser);
        assertEquals(userUpdateDto.getName(), updatedUser.getName());
        assertEquals(userUpdateDto.getEmail(), updatedUser.getEmail());

        Optional<User> userFromDb = userRepository.findById(savedUser.getId());
        assertTrue(userFromDb.isPresent());
        assertEquals("Updated Name", userFromDb.get().getName());
        assertEquals("updated.email@yandex.ru", userFromDb.get().getEmail());
    }

    @Test
    @DisplayName("Delete user. Successful delete")
    void shouldDeleteUserSuccessfully() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("User to Delete")
                .email("delete.user@yandex.ru")
                .build();

        UserDto savedUser = userService.saveUser(userCreateDto);

        UserDto deletedUser = userService.deleteUser(savedUser.getId());

        assertNotNull(deletedUser);
        assertEquals(savedUser.getName(), deletedUser.getName());

        Optional<User> userFromDb = userRepository.findById(savedUser.getId());
        assertFalse(userFromDb.isPresent());
    }

    @Test
    @DisplayName("Find user by ID. Successfully found")
    void shouldFindUserByIdSuccessfully() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("User Userov")
                .email("user@yandex.ru")
                .build();

        UserDto savedUser = userService.saveUser(userCreateDto);

        UserDto foundUser = userService.findUser(savedUser.getId());

        assertNotNull(foundUser);
        assertEquals(savedUser.getName(), foundUser.getName());
        assertEquals(savedUser.getEmail(), foundUser.getEmail());
    }

    @Test
    @DisplayName("Find all users. Successfully found")
    void shouldFindAllUsersSuccessfully() {
        UserCreateDto user1 = UserCreateDto.builder()
                .name("User One")
                .email("user.one@yandex.ru")
                .build();
        UserCreateDto user2 = UserCreateDto.builder()
                .name("User Two")
                .email("user.two@yandex.ru")
                .build();

        userService.saveUser(user1);
        userService.saveUser(user2);

        Collection<UserDto> allUsers = userService.findAll();

        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
    }
}