package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    //возвращает все существующие рейтинги MPA
    List<Mpa> getAllMpa();

    //возвращает определенный рейтинг MPA по id
    Mpa getMpaById(int mpaId);
}
