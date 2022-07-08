package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Service
public class GenreService {

    private final FilmStorage filmStorage;

    @Autowired
    public GenreService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Genre getGenreById(int id) {
        return filmStorage.getGenreById(id);
    }

    public Collection<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }
}