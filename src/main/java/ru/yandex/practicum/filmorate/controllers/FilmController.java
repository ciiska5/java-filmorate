package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    //добавляет фильм
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    //обновляет фильм
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    //получает все фильмы
    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    //получает каждый фильм по его уникальному идентификатору
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    //добавление пользователем лайка фильму
    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.likeFilm(id, userId);
    }

    //удаление пользователем лайка с фильма
    @DeleteMapping("/{id}/like/{userId}")
    public void removeFilmsLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeFilmsLike(id, userId);
    }

    //возвращает список из первых count фильмов по количеству лайков.
    //Если значение параметра count не задано, возращает первые 10 фильмов.
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}
