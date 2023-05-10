package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    //возвращает все MPA
    public List<Mpa> getAllMpa() {
        log.info("Запршены все MPA");
        return mpaDbStorage.getAllMpa();
    }

    //возвращает определенный MPA по id
    public Mpa getMpaById(int mpaId) {
        log.info("Запрошен MPA с id = {}", mpaId);
        return mpaDbStorage.getMpaById(mpaId);
    }
}
