package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.entity.User;

import java.util.Collection;

public interface UserStorage {
    User save(User user);

    User update(User user);

    User delete(Long id);

    User find(Long id);

    User findByEmail(String email);

    Collection<User> findAll();
}