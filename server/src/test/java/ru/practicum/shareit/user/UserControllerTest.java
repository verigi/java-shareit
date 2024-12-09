package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;


import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Create user")
    void shouldCreateUser() throws Exception {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("New User")
                .email("newuser@yandex.ru")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("New User")
                .email("newuser@yandex.ru")
                .build();

        Mockito.when(userService.saveUser(any(UserCreateDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("newuser@yandex.ru"));
    }

    @Test
    @DisplayName("Update user")
    void shouldUpdateUser() throws Exception {
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .name("Updated User")
                .email("updateduser@yandex.ru")
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("Updated User")
                .email("updateduser@yandex.ru")
                .build();

        Mockito.when(userService.updateUser(anyLong(), any(UserUpdateDto.class))).thenReturn(updatedUserDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.email").value("updateduser@yandex.ru"));
    }

    @Test
    @DisplayName("Delete user")
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("Get user by ID")
    void shouldGetUserById() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("User Name")
                .email("user@yandex.ru")
                .build();

        Mockito.when(userService.findUser(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("User Name"))
                .andExpect(jsonPath("$.email").value("user@yandex.ru"));
    }

    @Test
    @DisplayName("Get all users")
    void shouldGetAllUsers() throws Exception {
        UserDto user1 = UserDto.builder()
                .id(1L)
                .name("User One")
                .email("user1@yandex.ru")
                .build();

        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("User Two")
                .email("user2@yandex.ru")
                .build();

        Collection<UserDto> users = List.of(user1, user2);

        Mockito.when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("User One"))
                .andExpect(jsonPath("$[0].email").value("user1@yandex.ru"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("User Two"))
                .andExpect(jsonPath("$[1].email").value("user2@yandex.ru"));
    }
}