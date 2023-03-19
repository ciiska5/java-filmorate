package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    //создает пользователя
    User createUser(User user);

    //обновляет пользователя
    User updateUser(User user) throws ValidationException;

    //получает список всех пользователей
    Collection<User> getAllUsers();

    //получает пользователя по id
    User getUserById(int id);

    //добавляет друга
    void addFriend(int userId, int friendId);

    //удаляет из друзей
    void removeFriend(int userId, int friendId);

    //получет спсиок общих друзей
    List<User> getMutualFriends(int userId, int otherId);

    //возвращает список друзей пользователя
    List<User> getUsersFriends(int userId);
}
