package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmDaoObjectsTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Film createTestFilm() {
        Set<Integer> likes = new HashSet<>();
        List<Genre> genres = new ArrayList<>();
        Mpa mpa = new Mpa(1, "G");

        return new Film(
                1,
                "test name",
                "test description",
                LocalDate.parse("1967-03-25"),
                100L,
                likes,
                mpa,
                genres
        );
    }

    @SneakyThrows
    @DisplayName("Должен создавать фильм")
    @Test
    void shouldCreateFilm() {
        Film thisTestFilm = createTestFilm();

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestFilm)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @DisplayName("НЕ должен создавать фильм без названия")
    @Test
    void shouldNotCreateFilmWithEmptyName() {
        Film thisTestFilm = createTestFilm();
        thisTestFilm.setName("");

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestFilm)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @DisplayName("НЕ должен создавать фильм c описанием > 200 символов")
    @Test
    void shouldNotCreateFilmWithDescriptionAbove200Characters() {
        Film thisTestFilm = createTestFilm();
        thisTestFilm.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                "а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.");

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestFilm)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @DisplayName("НЕ должен создавать фильм c датой релиза до 1895_12_28")
    @Test
    void shouldNotCreateFilmWithReleaseDateBefore1895_12_28() {
        Film thisTestFilm = createTestFilm();
        thisTestFilm.setReleaseDate(LocalDate.parse("1890-03-25"));

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestFilm)))
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @DisplayName("НЕ должен создавать фильм c некорректной продолжительностью")
    @Test
    void shouldNotCreateFilmWithIncorrectDuration() {
        Film thisTestFilm = createTestFilm();

        thisTestFilm.setDuration(-100L);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestFilm)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @DisplayName("Должен обновлять фильм c правильно указанным id")
    @Test
    void shouldUpdateFilmWithCorrectId() {
        Film thisTestFilm = createTestFilm();

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestFilm)))
                .andExpect(status().is(200));

        thisTestFilm = new Film(
                1,
                "Film Updated",
                "New film update description",
                LocalDate.parse("1989-04-17"),
                190L,
                thisTestFilm.getLikes(),
                thisTestFilm.getMpa(),
                thisTestFilm.getGenres()
        );

        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestFilm)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @DisplayName("Не должен обновлять фильм c неправильно указанным id")
    @Test
    void shouldNotUpdateFilmWithIncorrectId() {
        Film thisTestFilm = createTestFilm();

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestFilm)))
                .andExpect(status().is(200));

        thisTestFilm = new Film(
                9999,
                "Film Updated",
                "New film update description",
                LocalDate.parse("1989-04-17"),
                190L,
                thisTestFilm.getLikes(),
                thisTestFilm.getMpa(),
                thisTestFilm.getGenres()
        );

        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestFilm)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @DisplayName("Должен вернуть все фильмы")
    @Test
    void shouldReturnFilmsList() {
        Film thisTestFilm = createTestFilm();

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(thisTestFilm)))
                .andExpect(status().is(200));

        mockMvc.perform(get("/films"))
                .andExpect(status().is(200));
    }
}
