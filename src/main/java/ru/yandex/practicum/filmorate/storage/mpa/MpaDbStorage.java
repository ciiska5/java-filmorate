package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("MpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    public final JdbcTemplate jdbcTemplate;

    //возвращает все существующие рейтинги MPA
    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM MPA_ratings;";

        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    //имплементация RowMapper
    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {

        return Mpa.builder()
                .id(resultSet.getInt("MPA_rating_id"))
                .name(resultSet.getString("MPA_name"))
                .build();
    }


    //возвращает определенный рейтинг MPA по id
    @Override
    public Mpa getMpaById(int mpaId) {
        String sql = "SELECT * FROM MPA_ratings " +
                "WHERE MPA_rating_id = ?;";

        SqlRowSet mpaRowSet = jdbcTemplate.queryForRowSet(sql, mpaId);

        if (mpaRowSet.next()) {
            return new Mpa(
                    mpaRowSet.getInt("MPA_rating_id"),
                    mpaRowSet.getString("MPA_name")
            );
        } else {
            throw new MpaNotFoundException("Рейтинг MPA не найден");
        }
    }
}
