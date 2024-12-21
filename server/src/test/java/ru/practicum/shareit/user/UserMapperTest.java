package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("User to UserDto")
    void toDto_shouldConvertUserToUserDto() {
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@yandex.ru")
                .build();

        UserDto userDto = userMapper.toDto(user);

        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals("user", userDto.getName());
        assertEquals("user@yandex.ru", userDto.getEmail());
    }

    @Test
    @DisplayName("User to UserDto. Null user")
    void toDto_shouldReturnNullForNullUser() {
        UserDto userDto = userMapper.toDto(null);

        assertNull(userDto);
    }

    @Test
    @DisplayName("UserCreateDto to User")
    void toUser_shouldConvertUserCreateDtoToUser() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("user")
                .email("user@yandex.ru")
                .build();

        User user = userMapper.toUser(userCreateDto);

        assertNotNull(user);
        assertEquals("user", user.getName());
        assertEquals("user@yandex.ru", user.getEmail());
    }

    @Test
    @DisplayName("UserCreateDto to User. Null DTO")
    void toUser_shouldReturnNullForNullUserCreateDto() {
        User user = userMapper.toUser(null);

        assertNull(user);
    }

    @Test
    @DisplayName("User list to DTO list")
    void toDtoList_shouldConvertListOfUsersToListOfUserDtos() {
        List<User> users = List.of(
                User.builder().id(1L).name("user 1").email("user1@yandex.ru").build(),
                User.builder().id(2L).name("user 2").email("user2@yandex.ru").build()
        );

        List<UserDto> userDtos = userMapper.toDtoList(users);

        assertNotNull(userDtos);
        assertEquals(2, userDtos.size());
        assertEquals("user 1", userDtos.get(0).getName());
        assertEquals("user2@yandex.ru", userDtos.get(1).getEmail());
    }

    @Test
    @DisplayName("User update from DTO")
    void updateUserFromDto_shouldUpdateUserFromUserDto() {
        User user = User.builder().id(1L).name("old name").email("old.email@yandex.ru").build();
        UserDto userDto = UserDto.builder().name("new name").email("new.email@yandex.ru").build();

        User updatedUser = userMapper.updateUserFromDto(userDto, user);

        assertNotNull(updatedUser);
        assertEquals("new name", updatedUser.getName());
        assertEquals("new.email@yandex.ru", updatedUser.getEmail());
    }

    @Test
    @DisplayName("User update from UserUpdateDto")
    void updateUserFromDto_shouldUpdateUserFromUserUpdateDto() {
        User user = User.builder().id(1L).name("old name").email("old.email@yandex.ru").build();
        UserUpdateDto userUpdateDto = UserUpdateDto.builder().name("updated name").email("updated.email@yandex.ru").build();

        User updatedUser = userMapper.updateUserFromDto(userUpdateDto, user);

        assertNotNull(updatedUser);
        assertEquals("updated name", updatedUser.getName());
        assertEquals("updated.email@yandex.ru", updatedUser.getEmail());
    }

    @Test
    @DisplayName("User update with partial UserUpdateDto")
    void updateUserFromDto_shouldPartiallyUpdateUser() {
        User user = User.builder().id(1L).name("old name").email("old.email@yandex.ru").build();
        UserUpdateDto userUpdateDto = UserUpdateDto.builder().name("new partial name").build();

        User updatedUser = userMapper.updateUserFromDto(userUpdateDto, user);

        assertNotNull(updatedUser);
        assertEquals("new partial name", updatedUser.getName());
        assertEquals("old.email@yandex.ru", updatedUser.getEmail());
    }
}