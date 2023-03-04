package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    private Integer filmId = 1;

    private final static LocalDate MIN_FILM_RELEASE_DATE = LocalDate.parse("1895-12-28");

    //добавляет фильм
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(MIN_FILM_RELEASE_DATE)) {
            log.error("Дата релиза - раньше 28 декабря 1985");
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1985");
        }
        film.setId(incrementFilmId());
        films.put(film.getId(), film);
        log.trace(String.valueOf(film));
        return film;
    }

    //обновляет фильм
    @PutMapping()
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.trace(String.valueOf(film));
            return film;
        } else {
            log.error("Не найден фильм с id = {}", film.getId());
            throw new ValidationException("Фильм с указанным id не найден");
        }
    }

    //получает все фильмы
    @GetMapping
    public Collection<Film> getFilms() {
        log.trace("Общее количество фильмов {}: ", films.size());
        return films.values();
    }

    //инкрементирует filmId
    private Integer incrementFilmId() {
        return filmId++;
    }

}
