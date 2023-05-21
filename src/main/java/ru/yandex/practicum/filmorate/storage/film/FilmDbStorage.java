package ru.yandex.practicum.filmorate.storage.film;
//класс DAO

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("FilmDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    private static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.parse("1895-12-28");

    //добавляет фильм в БД
    @Override
    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_FILM_RELEASE_DATE)) {
            log.error("Дата релиза - раньше 28 декабря 1985");
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1985");
        }

        String sql = "INSERT INTO films (MPA_rating_id, name, description, release_date, duration) " +
                "VALUES (?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setInt(1, film.getMpa().getId());
            stmt.setString(2, film.getName());
            stmt.setString(3, film.getDescription());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(5, film.getDuration());
            return stmt;
        }, keyHolder);

        int filmId = (Objects.requireNonNull(keyHolder.getKey())).intValue();
        film.setId(filmId);

        if (film.getGenres() != null) {
            List<Genre> genres = removeDuplicateGenre(film);
            genreStorage.addGenresToFilm(filmId, genres);
        }

        return film;
    }

    //удаляет повторяющиеся жанры
    private List<Genre> removeDuplicateGenre(Film film) {
        film.setGenres(
                film.getGenres().stream().distinct().collect(Collectors.toList())
        );
        return film.getGenres();
    }

    //обновляет фильм в БД
    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET MPA_rating_id = ?, name = ?, description = ?, release_date = ?, duration = ? " +
                "WHERE film_id = ?;";

        jdbcTemplate.update(sql,
                film.getMpa().getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()
        );


        if (film.getGenres() != null) {
            List<Genre> genres = removeDuplicateGenre(film);
            genreStorage.removeGenreFromFilm(film.getId());
            genreStorage.addGenresToFilm(film.getId(), genres);
        }

        return getFilmById(film.getId());
    }

    //возвращает все фильмы из БД
    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT * FROM films AS f " +
                "LEFT JOIN MPA_ratings AS m " +
                "ON m.MPA_rating_id = f.MPA_rating_id;";

        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    //имплементация RowMapper
    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        int filmId = resultSet.getInt("film_id");

        Film film = Film.builder()
                .id(filmId)
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(LocalDate.parse(resultSet.getString("release_date")))
                .duration(resultSet.getLong("duration"))
                .mpa(new Mpa(resultSet.getInt("MPA_rating_id"), resultSet.getString("MPA_name")))
                .build();

        film.setGenres(genreStorage.getGenresByFilmId(filmId));
        return film;
    }

    //возвращает фильм из БД по id фильма
    @Override
    public Film getFilmById(int filmId) {
        String sql = "SELECT * FROM films AS f " +
                "LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN MPA_ratings AS mr ON f.MPA_rating_id = mr.MPA_rating_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE f.film_id = ?;";

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, filmId);

        if (films.size() > 0) {
            return films.get(0);
        } else {
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    //возвращает наиболее популярные фильмы по кличеству лайков
    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT * FROM films AS f " +
                "JOIN MPA_ratings AS mr ON f.MPA_rating_id = mr.MPA_rating_id " +
                "LEFT JOIN (SELECT film_id, " +
                "COUNT(user_id) AS count " +
                "FROM likes " +
                "GROUP BY film_id) AS filmLikes " +
                "ON f.film_id = filmLikes.film_id " +
                "ORDER BY filmLikes.count DESC " +
                "LIMIT ?;";

        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    //добавляет лайк пользователя в БД
    @Override
    public void likeFilm(int filmId, int userId) {
        if (getFilmById(filmId) != null) {
            String sql =
                    "MERGE INTO likes KEY(film_id, user_id) VALUES (?, ?);";

            jdbcTemplate.update(sql, filmId, userId);
        } else {
            throw new FilmNotFoundException("Фильм не найден");
        }
    }

    //убирает лайк пользователя из БД
    @Override
    public void removeFilmsLike(int filmId, int userId) {
        String sql = "SELECT * FROM likes " +
                "WHERE film_id = ? AND user_id = ?;";

        SqlRowSet likesRow = jdbcTemplate.queryForRowSet(sql, filmId, userId);

        if (likesRow.next()) {
            String deleteSql = "DELETE FROM likes " +
                    "WHERE film_id = ? AND user_id = ?;";
            jdbcTemplate.update(deleteSql, filmId, userId);
        } else {
            throw new FilmNotFoundException("Фильм не найден");
        }
    }
}
