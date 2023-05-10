package ru.yandex.practicum.filmorate.storage.user;
//класс DAO

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component("UserDbStorage")
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    //создает пользователя
    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        SqlParameterSource parameterSources = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());

        Number userId = simpleJdbcInsert.executeAndReturnKey(parameterSources);
        user.setId(userId.intValue());
        return user;
    }

    @Override
    //обновляет пользователя в БД
    public User updateUser(User user) {
        if (getUserById(user.getId()) != null) {
            String sql = "MERGE INTO users (user_id, email, login, name, birthday) KEY (user_id) VALUES (?, ?, ?, ?, ?);";

            jdbcTemplate.update(sql,
                    user.getId(),
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday()
            );

            return user;
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    //получает всех пользователей из БД
    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users";

        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    //имплементация RowMapper
    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {

        int userId = resultSet.getInt("user_id");

        return User.builder()
                .id(userId)
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(LocalDate.parse(resultSet.getString("birthday")))
                .build();
    }

    //получает пользователя по id из БД
    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?;";

        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sql, id);

        if (userRow.next()) {
            return new User(
                    userRow.getInt("user_id"),
                    userRow.getString("email"),
                    userRow.getString("login"),
                    userRow.getString("name"),
                    LocalDate.parse(Objects.requireNonNull(userRow.getString("birthday")))
            );
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    //добавляет друга
    @Override
    public void addFriend(int userId, int friendId) {
        if (getUserById(userId) != null && getUserById(friendId) != null) {
            String sql = "INSERT INTO friendship_status " +
                    "(user_id_from, user_id_to, are_friends) " +
                    "VALUES (?, ?, ?);";
            jdbcTemplate.update(sql, userId, friendId, true);
        } else {
            if (getUserById(userId) == null && getUserById(friendId) == null) {
                throw new UserNotFoundException("Пользователи не найдены");
            } else if (getUserById(userId) == null) {
                throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId));
            }
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", friendId));
        }
    }

    //удаляет друга
    @Override
    public void removeFriend(int userId, int friendId) {
        if (getUserById(userId) != null && getUserById(friendId) != null) {
            String sql = "DELETE FROM friendship_status " +
                    "WHERE user_id_from = ? AND  user_id_to = ?;";
            jdbcTemplate.update(sql, userId, friendId);
        } else {
            if (getUserById(userId) == null && getUserById(friendId) == null) {
                throw new UserNotFoundException("Пользователи не найдены");
            } else if (getUserById(userId) == null) {
                throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", userId));
            }
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден", friendId));
        }
    }

    //получет спсиок общих друзей
    @Override
    public List<User> getMutualFriends(int userId, int otherId) {
        String sql = "SELECT * FROM users AS u " +
                "WHERE u.user_id IN " +
                "    (SELECT user_id_to AS id FROM friendship_status WHERE user_id_from = ? " +
                "    UNION " +
                "    SELECT user_id_from AS id FROM friendship_status WHERE user_id_to = ?) " +
                "AND u.user_id IN " +
                "    (SELECT user_id_to AS id FROM friendship_status WHERE user_id_from = ? " +
                "    UNION " +
                "    SELECT user_id_from AS id FROM friendship_status WHERE user_id_to = ?) ";

        return jdbcTemplate.query(sql, this::mapRowToUser, userId, userId, otherId, otherId);
    }

    //возвращает список друзей пользователя
    @Override
    public List<User> getUsersFriends(int userId) {
        String sql = "SELECT * FROM users AS u " +
                "WHERE u.user_id IN " +
                "    (SELECT user_id_to AS id FROM friendship_status " +
                "     WHERE user_id_from = ?);";

        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }
}
