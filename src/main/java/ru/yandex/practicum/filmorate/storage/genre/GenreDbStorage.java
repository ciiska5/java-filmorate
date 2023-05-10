package ru.yandex.practicum.filmorate.storage.genre;
//класс DAO

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("GenreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //получение списка всех жанров
    @Override
    public List<Genre> getGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    //имплементация RowMapper
    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    //получение жанра по id
    @Override
    public Genre getGenreById(int genreId) {
        String sql = "SELECT * FROM genres " +
                "WHERE genre_id = ?;";

        SqlRowSet genreRowSet = jdbcTemplate.queryForRowSet(sql, genreId);

        if (genreRowSet.next()) {
            return new Genre(
                    genreRowSet.getInt("genre_id"),
                    genreRowSet.getString("name")
            );
        } else {
            throw new GenreNotFoundException("Жанр не найден");
        }
    }

    //получение жанров определенного фильма
    @Override
    public List<Genre> getGenresByFilmId(int filmId) {
        String sql = "SELECT * FROM genres AS g " +
                "LEFT JOIN film_genre AS fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?;";

        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    //присвоение списка жанров определенному фильму
    @Override
    public void addGenresToFilm(int filmId, List<Genre> genres) {
        String sql = "MERGE INTO film_genre (film_id, genre_id) " +
                "VALUES (?, ?);";

        for (Genre genre : genres) {
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
    }

    //удаление жанра из фильма
    @Override
    public void removeGenreFromFilm(int filmId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?;";

        jdbcTemplate.update(sql, filmId);
    }
}
