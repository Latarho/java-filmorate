package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.ValidationException;

public class FilmControllerTests {

    FilmService filmService = new FilmService(new InMemoryFilmStorage(), new UserService(new InMemoryUserStorage()));

    @Test
    void checkValidationExceptionReleaseDate() {
        FilmDto filmOne = new FilmDto(
                null,
                "Фильм номер один",
                "У этого фильма дата релиза раньше 1895",
                "1890-01-01",
                9000L);
        Assertions.assertThrows(ValidationException.class, () -> filmService.validationFilm(filmOne));
    }

    @Test
    void checkValidationExceptionDuration() {
        FilmDto filmOne = new FilmDto(
                null,
                "Фильм номер один",
                "У этого фильма отрицательная продолжительность",
                "1990-01-01",
                -9000L);
        Assertions.assertThrows(ValidationException.class, () -> filmService.validationFilm(filmOne));
    }
}