package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreStorage {

    Genre getGenreById(int id);

    Collection<Genre> getAllGenres();

    void addGenreForFilm(Long filmId, Integer genreId);

    void deleteGenreForFilm(Long filmId);
}