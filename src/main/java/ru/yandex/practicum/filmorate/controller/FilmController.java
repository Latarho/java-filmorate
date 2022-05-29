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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

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

    /**
     * Генерация уникального Id фильма.
     *
     * @return Id фильма.
     */
    private int generateId() {
        return id++;
    }
}