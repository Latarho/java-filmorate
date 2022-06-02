package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ValidationException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTests {

    FilmController filmController = new FilmController();

    @Test
    void checkValidationExceptionReleaseDate() {
        Film filmOne = new Film("Фильм номер один", "У этого фильма дата релиза раньше 1895",
                                LocalDate.of(1894, 12, 28), Duration.of(200, ChronoUnit.MINUTES));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validationFilm(filmOne));
        assertEquals("Дата релиза фильма не может быть раньше 28 января 1895 года", exception.getMessage());
    }

    @Test
    void checkValidationExceptionDuration() {
        Film filmOne = new Film("Фильм номер один", "У этого фильма отрицательная продолжительность",
                LocalDate.of(1899, 12, 28), Duration.of(-1, ChronoUnit.MINUTES));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.validationFilm(filmOne));
        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }
}