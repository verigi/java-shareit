package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NoSuchUserException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Component("MemoryUserStorage")
public class MemoryUserStorage implements UserStorage {
    HashMap<Long, User> storage = new HashMap<>();

    @Override
    public User save(User user) {
        log.debug("Adding user {} to storage ", user.getName());
        user.setId(generateId());
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("Updating user {} in storage", user.getId());
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(Long id) {
        log.debug("Deleting user {} from storage", id);
        return Optional.ofNullable(storage.remove(id)).orElseThrow(() -> {
            log.warn("Incorrect user id " + id);
            throw new NoSuchUserException("Incorrect id");
        });
    }

    @Override
    public User find(Long id) {
        log.debug("Finding user {} from storage", id);
        return Optional.ofNullable(storage.get(id)).orElseThrow(() -> {
            log.warn("Incorrect user id: " + id);
            throw new NoSuchUserException("Incorrect id");
        });
    }

    @Override
    public User findByEmail(String email) {
        log.debug("Getting user by email: {}", email);
        return storage.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new NoSuchUserException("User not found"));
    }

    @Override
    public Collection<User> findAll() {
        log.debug("Getting all users from storage");
        return storage.values();
    }

    private long generateId() {
        log.debug("Generating id for user");
        long currId = storage.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currId;
    }
}
