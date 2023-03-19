package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UpdateException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    public Integer userId = 1;

    //инкрементирует userId
    private Integer incrementUserId() {
        return userId++;
    }

    //создает пользователя
    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Задано пустое имя пользователя или имя пользователя не задано");
            user.setName(user.getLogin());
        }
        user.setId(incrementUserId());
        users.put(user.getId(), user);
        log.trace(String.valueOf(user));
        return user;
    }

    //обновляет пользователя
    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.trace(String.valueOf(user));
            return user;
        } else {
            log.error("Не найден пользователь с id = {}", user.getId());
            throw new UpdateException("Пользователь с указанным id не найден");
        }
    }

    //получает список всех пользователей
    @Override
    public Collection<User> getAllUsers() {
        log.trace("Общее количество пользователей: {}", users.size());
        return users.values();
    }

    //получает пользователя по id
    @Override
    public User getUserById(int userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }

    //добавляет друга
    @Override
    public void addFriend(int userId, int friendId) {
        if (users.containsKey(userId) && users.containsKey(friendId)) {
            if (users.get(userId).getFriends().contains(friendId)) {
                throw new ValidationException(
                        String.format("Пользователь с id = %d уже в друзьях у пользователя с id = %d", friendId, userId)
                );
            }
            users.get(userId).getFriends().add(friendId);
            users.get(friendId).getFriends().add(userId);
        } else {
            throw new UserNotFoundException("Пользовател(и) не найдены");
        }
    }

    //удаляет из друзей
    @Override
    public void removeFriend(int userId, int friendId) {
        if (users.containsKey(userId) && users.containsKey(friendId)) {
            if (!users.get(userId).getFriends().contains(friendId)) {
                throw new UserNotFoundException(
                        String.format("Пользователь с id = %d не найден в друзьях у пользователя с id = %d", friendId, userId)
                );
            }
            users.get(userId).getFriends().remove(friendId);
            users.get(friendId).getFriends().remove(userId);
        } else {
            throw new UserNotFoundException("Пользовател(и) не найдены");
        }
    }

    //получет спсиок общих друзей
    @Override
    public List<User> getMutualFriends(int userId, int otherId) {
        List<User> mutualFriends = new ArrayList<>();
        if (users.containsKey(userId) && users.containsKey(otherId)) {
            Set<Integer> usersFriends = new HashSet<>(users.get(userId).getFriends());
            Set<Integer> otherUsersFriends = new HashSet<>(users.get(otherId).getFriends());
            usersFriends.retainAll(otherUsersFriends);
            for (Integer id : usersFriends) {
                mutualFriends.add(users.get(id));
            }
            return mutualFriends;
        } else {
            throw new UserNotFoundException("Пользователи не найдены");
        }
    }

    //возвращает список друзей пользователя
    @Override
    public List<User> getUsersFriends(int userId) {
        List<User> usersFriends = new ArrayList<>();
        if (users.containsKey(userId)) {
            Set<Integer> friendsId = users.get(userId).getFriends();
            if (friendsId.isEmpty()) {
                throw new UserNotFoundException("Список друзей пуст");
            }
            for (Integer id : friendsId) {
                usersFriends.add(users.get(id));
            }
            return usersFriends;
        } else {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }
}
