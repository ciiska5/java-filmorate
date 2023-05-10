package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    //добавляет фильм
    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    //обновляет фильм
    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    //получает все фильмы
    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    //получает фильм по id
    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    //получает наиболее популярные фильмы по количеству лайков
    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    //(пользователь) лайкает фильм
    public void likeFilm(int filmId, int userId) {
        filmStorage.likeFilm(filmId, userId);
    }

    //(пользователь) убирает лайк из фильма
    public void removeFilmsLike(int filmId, int userId) {
        filmStorage.removeFilmsLike(filmId, userId);
    }
}
