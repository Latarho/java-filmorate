package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film addFilm(Film film);

    Optional<Film> getFilmById(Long id);

    List<Film> getAllFilms();

    Film updateFilm(Film film);

    void likeFilm(Long filmId, Long userId);

    void unlikeFilm(Long filmId, Long userId);

    Collection<Film> getPopular(Integer count);

    MpaRating getMpaRatingById(int id);

    Collection<MpaRating> getAllMpa();

    Genre getGenreById(int id);

    Collection<Genre> getAllGenres();
}