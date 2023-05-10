package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UpdateException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer filmId = 1;
    private static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.parse("1895-12-28");

    //инкрементирует filmId
    private Integer incrementFilmId() {
        return filmId++;
    }

    //добавляет фильм
    @Override
    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_FILM_RELEASE_DATE)) {
            log.error("Дата релиза - раньше 28 декабря 1985");
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1985");
        }
        film.setId(incrementFilmId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм с id = {}", film.getId());
        log.trace(String.valueOf(film));
        return film;
    }

    //обновляет фильм
    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлен фильм с id = {}", film.getId());
            log.trace(String.valueOf(film));
            return film;
        } else {
            log.error("Не найден фильм с id = {}", film.getId());
            throw new UpdateException("Фильм с указанным id не найден");
        }
    }

    //получает все фильмы
    @Override
    public Collection<Film> getFilms() {
        log.info("Общее количество фильмов {}: ", films.size());
        return films.values();
    }

    //получает фильм по id
    @Override
    public Film getFilmById(int filmId) {
        if (films.containsKey(filmId)) {
            Film film = films.get(filmId);
            log.trace(String.valueOf(film));
            return film;
        } else {
            log.error("Не найден фильм с id = {}", filmId);
            throw new FilmNotFoundException(String.format("Фильм с id = %d не найден", filmId));
        }
    }

    //получает наиболее популярные фильмы по количеству лайков
    @Override
    public List<Film> getPopularFilms(int count) {
        log.info("Получны наиболее популярные фильмы в количестве {}: ", count);
        return getFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size()) //сортировка по убыванию кол-ва лайков
                .limit(count)
                .collect(Collectors.toList());
    }

    //(пользователь) лайкает фильм
    @Override
    public void likeFilm(int filmId, int userId) {
        if (films.containsKey(filmId)) {
            Film film = films.get(filmId);
            if (!film.getLikes().contains(userId)) {
                film.getLikes().add(userId);
            } else {
                log.error("Пользователь с id = {} уже лайкал этот фильм", userId);
                throw new UserNotFoundException(String.format("Пользователь с id = %d уже лайкал этот фильм", userId));
            }
        } else {
            log.error("Фильм с id = {} не найден", filmId);
            throw new FilmNotFoundException(String.format("Фильм с id = %d не найден", filmId));
        }
    }

    //(пользователь) убирает лайк из фильма
    @Override
    public void removeFilmsLike(int filmId, int userId) {
        if (films.containsKey(filmId)) {
            Film film = films.get(filmId);
            if (film.getLikes().contains(userId)) {
                film.getLikes().remove(userId);
            } else {
                log.error("Пользователь с id = {} не лайкал этот фильм", userId);
                throw new UserNotFoundException(String.format("Пользователь с id = %d не лайкал этот фильм", userId));
            }
        } else {
            log.error("Фильм с id = {} не найден", filmId);
            throw new FilmNotFoundException(String.format("Фильм с id = %d не найден", filmId));
        }
    }
}
