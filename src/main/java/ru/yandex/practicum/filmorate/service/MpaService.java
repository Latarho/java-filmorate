package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Service
public class MpaService {

    private final FilmStorage filmStorage;

    @Autowired
    public MpaService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public MpaRating getMpaRatingById(int id) {
        return filmStorage.getMpaRatingById(id);
    }

    public Collection<MpaRating> getAllMpa() {
        return filmStorage.getAllMpa();
    }
}