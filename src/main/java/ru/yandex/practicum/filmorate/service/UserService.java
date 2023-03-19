package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    //создает пользователя
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    //обновляет пользователя
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    //получает список всех пользователей
    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    //получает пользователя по id
    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    //добавляет друга
    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    //удаляет из друзей
    public void removeFriend(int userId, int friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    //получет спсиок общих друзей
    public List<User> getMutualFriends(int userId, int anotherUserId) {
        return userStorage.getMutualFriends(userId, anotherUserId);
    }

    //возвращает список друзей пользователя
    public List<User> getUsersFriends(int userId) {
        return userStorage.getUsersFriends(userId);
    }
}
