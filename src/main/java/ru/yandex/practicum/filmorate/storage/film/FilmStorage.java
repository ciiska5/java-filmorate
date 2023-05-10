package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    //добавляет фильм
    Film addFilm(Film film);

    //обновляет фильм
    Film updateFilm(Film film);

    //получает все фильмы
    Collection<Film> getFilms();

    //получает фильм по id
    Film getFilmById(int filmId);

    //получает наиболее популярные фильмы по количеству лайков
    List<Film> getPopularFilms(int count);

    //(пользователь) лайкает фильм
    void likeFilm(int filmId, int userId);

    //(пользователь) убирает лайк из фильма
    void removeFilmsLike(int filmId, int userId);

}
