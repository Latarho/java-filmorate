package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private Long id = 1L;

    /**
     * Добавление нового фильма.
     *
     * @param film объект класса Film (из тела запроса).
     * @param request запрос.
     * @return объект класса Film.
     */
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        try {
            log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                    request.getRequestURI(), film);
            validationFilm(film);
            film.setId(generateId());
            films.put(film.getId(), film);
            log.info("Фильм: {} успешно добавлен в фильмотеку", film);
        } catch (ValidationException exc) {
            log.info("Произошла ошибка валидации: "+ exc.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exc.getMessage());
        }
        return film;
    }

    /**
     * Обновление существующего фильма.
     *
     * @param film объект класса Film (из тела запроса).
     * @param request запрос.
     * @return объект класса Film.
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        try {
            log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                    request.getRequestURI(), film);
            if (!films.containsKey(film.getId())) {
                log.info("Фильм: {} не найден", film);
                throw new FilmNotFoundException("Фильм: " + film + "не найден");
            } else {
                validationFilm(film);
                films.put(film.getId(), film);
                log.info("Фильм: {} успешно обновлен в фильмотеке", film);
            }
        } catch (ValidationException exc) {
            log.info("Произошла ошибка валидации: "+ exc.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exc.getMessage());
        }
        return film;
    }

    /**
     * Получение списка фильмов.
     *
     * @return список фильмов.
     */
    @GetMapping
    public ArrayList<Film> getAllFilms() {
        ArrayList<Film> filmsList = new ArrayList<>();
        filmsList.addAll(films.values());
        log.info("В списке filmsList содержится {} фильмов", filmsList.size());
        return filmsList;
    }

    protected void validationFilm(Film film) {
        LocalDate date = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(date)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 января 1895 года");
        }
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    /**
     * Генерация уникального Id фильма.
     *
     * @return Id фильма.
     */
    private Long generateId() {
        return id++;
    }
}