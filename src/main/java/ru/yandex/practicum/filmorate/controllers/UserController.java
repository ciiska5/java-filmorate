package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    public Integer userId = 1;

    //создает пользователя
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(incrementUserId());
        users.put(user.getId(), user);
        log.trace(String.valueOf(user));
        return user;
    }

    //обновляет пользователя
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.trace(String.valueOf(user));
            return user;
        } else {
            log.error("Не найден пользователь с id = {}", user.getId());
            throw new ValidationException("Пользователь с указанным id не найден");
        }
    }

    //получает список всех пользователей
    @GetMapping
    public Collection<User> getAllUsers() {
        log.trace("Общее количество пользователей: {}", users.size());
        return users.values();
    }

    //инкрементирует userId
    private Integer incrementUserId() {
        return userId++;
    }
}
