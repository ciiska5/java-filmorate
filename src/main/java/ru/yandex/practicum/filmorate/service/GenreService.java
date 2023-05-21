package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    //возвращает список всех жанров
    public List<Genre> getAllGenres() {
        log.info("Запрошен список всех жанров");
        return genreDbStorage.getGenres();
    }

    //возвращает определенный жанр
    public Genre getGenreById(int genreId) {
        log.info("Запрошен жанр c id = {}", genreId);
        return genreDbStorage.getGenreById(genreId);
    }
}
