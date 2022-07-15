package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film addFilm(Film film);

    Optional<Film> getFilmById(Long id);

    List<Film> getAllFilms();

    Film updateFilm(Film film);

    void deleteById(Long id);

    void likeFilm(Long filmId, Long userId);

    void unlikeFilm(Long filmId, Long userId);

    Collection<Film> getPopular(Integer count);
}