package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    //получение списка всех жанров
    List<Genre> getGenres();

    //получение жанра по id
    Genre getGenreById(int genreId);

    //получение жанров определенного фильма
    List<Genre> getGenresByFilmId(int filmId);

    //присвоение списка жанров определенному фильму
    void addGenresToFilm(int filmId, List<Genre> genres);

    //удаление жанра из фильма
    void removeGenreFromFilm(int filmId);
}
