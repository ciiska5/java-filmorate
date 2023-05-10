package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UpdateException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer userId = 1;

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
        log.info("Добавлен пользователь с id = {}", user.getId());
        users.put(user.getId(), user);
        log.trace(String.valueOf(user));
        return user;
    }

    //обновляет пользователя
    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь с id = {}", user.getId());
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
        log.info("Общее количество пользователей: {}", users.size());
        return users.values();
    }

    //получает пользователя по id
    @Override
    public User getUserById(int userId) {
        if (users.containsKey(userId)) {
            User user = users.get(userId);
            log.trace(String.valueOf(user));
            return users.get(userId);
        } else {
            log.error("Не найден пользователь с id = {}", userId);
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }

    //добавляет друга
    @Override
    public void addFriend(int userId, int friendId) {
        boolean ifUser = users.containsKey(userId);
        boolean ifFriend = users.containsKey(friendId);
        if (ifUser && ifFriend) {
            User user = users.get(userId);
            if (user.getFriends().contains(friendId)) {
                log.error("Пользователь с id = {} уже в друзьях у пользователя с id = {}", friendId, userId);
                throw new ValidationException(
                        String.format("Пользователь с id = %d уже в друзьях у пользователя с id = %d", friendId, userId)
                );
            }
            log.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", userId, friendId);
            user.getFriends().add(friendId);
            users.get(friendId).getFriends().add(userId);
        } else {
            if (!ifUser && !ifFriend) {
                log.error("Пользователи не найдены");
                throw new UserNotFoundException("Пользователи не найдены");
            } else if (!ifUser) {
                log.error("Пользователь с id = {} не найден", userId);
                throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId));
            }
            log.error("Пользователь с id = {} не найден", friendId);
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", friendId));
        }
    }

    //удаляет из друзей
    @Override
    public void removeFriend(int userId, int friendId) {
        boolean ifUser = users.containsKey(userId);
        boolean ifFriend = users.containsKey(friendId);
        if (ifUser && ifFriend) {
            User user = users.get(userId);
            if (!user.getFriends().contains(friendId)) {
                log.error("Пользователь с id = {} не найден в друзьях у пользователя с id = {}", friendId, userId);
                throw new UserNotFoundException(
                        String.format("Пользователь с id = %d не найден в друзьях у пользователя с id = %d", friendId, userId)
                );
            }
            log.info("Пользователь с id = {} удалил из друзей пользователя с id = {}", userId, friendId);
            user.getFriends().remove(friendId);
            users.get(friendId).getFriends().remove(userId);
        } else {
            if (!ifUser && !ifFriend) {
                log.error("Пользователи не найдены");
                throw new UserNotFoundException("Пользователи не найдены");
            } else if (!ifUser) {
                log.error("Пользователь с id = {} не найден", userId);
                throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId));
            }
            log.error("Пользователь с id = {} не найден", friendId);
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", friendId));
        }
    }

    //получет спсиок общих друзей
    @Override
    public List<User> getMutualFriends(int userId, int otherId) {
        List<User> mutualFriends = new ArrayList<>();
        boolean ifUser = users.containsKey(userId);
        boolean ifOther = users.containsKey(otherId);
        if (ifUser && ifOther) {
            Set<Integer> usersFriends = new HashSet<>(users.get(userId).getFriends());
            Set<Integer> otherUsersFriends = new HashSet<>(users.get(otherId).getFriends());
            usersFriends.retainAll(otherUsersFriends);
            for (Integer id : usersFriends) {
                mutualFriends.add(users.get(id));
            }
            log.info("Получен список общих друзей");
            return mutualFriends;
        } else {
            if (!ifUser && !ifOther) {
                log.error("Пользователи не найдены");
                throw new UserNotFoundException("Пользователи не найдены");
            } else if (!ifUser) {
                log.error("Пользователь с id = {} не найден", userId);
                throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId));
            }
            log.error("Пользователь с id = {} не найден", otherId);
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", otherId));
        }
    }

    //возвращает список друзей пользователя
    @Override
    public List<User> getUsersFriends(int userId) {
        List<User> usersFriends = new ArrayList<>();
        if (users.containsKey(userId)) {
            Set<Integer> friendsId = users.get(userId).getFriends();
            if (friendsId.isEmpty()) {
                log.error("Список друзей пуст");
                throw new UserNotFoundException("Список друзей пуст");
            }
            for (Integer id : friendsId) {
                usersFriends.add(users.get(id));
            }
            log.info("Получен список друзей пользователя с id = {}", userId);
            return usersFriends;
        } else {
            log.error("Пользователь с id = {} не найден", userId);
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }
}
