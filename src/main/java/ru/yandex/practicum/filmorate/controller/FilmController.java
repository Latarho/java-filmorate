package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    /**
     * Добавление нового фильма.
     *
     * @param filmDto объект класса Film (из тела запроса).
     * @param request запрос.
     * @return объект класса Film.
     */
    @PostMapping
    public Film addFilm(@Valid @RequestBody FilmDto filmDto, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                request.getRequestURI(), filmDto);
        Film newFilm = filmService.addFilm(filmDto);
        log.info("Фильм: {} успешно добавлен в фильмотеку", filmDto.getName());
        return newFilm;
    }

    /**
     * Получение значения (объект класса Film) по переданному id.
     *
     * @param id идентификатор Film.
     * @param request запрос.
     * @return объект класса Film соответствующее переданному id.
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(),
                request.getRequestURI());
        Film film = filmService.getFilmById(id);
        log.info("Фильм: {} успешно найден", film.getName());
        return film;
    }

    /**
     * Получение списка фильмов.
     *
     * @return список фильмов.
     */
    @GetMapping
    public List<Film> getAllFilms(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(),
                request.getRequestURI());
        List<Film> filmsList = filmService.getAllFilms();
        log.info("В списке filmsList содержится {} фильмов", filmsList.size());
        return filmsList;
    }

    /**
     * Обновление существующего фильма.
     *
     * @param filmDto объект класса Film (из тела запроса).
     * @param request запрос.
     * @return объект класса Film.
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody FilmDto filmDto, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                request.getRequestURI(), filmDto);
        Film film = filmService.updateFilm(filmDto);
        log.info("Фильм: {} успешно обновлен", filmDto);
        return film;
    }

    //TODO дописать доку
    @PutMapping("/{filmId}/like/{userId}")
    public void like(@PathVariable Long filmId, @PathVariable Long userId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(),
                request.getRequestURI());
        filmService.likeFilm(filmId, userId);
        log.info("Пользователь: {} поставил лайк фильму: {}", userId, filmId);
    }

    //TODO дописать доку
    @DeleteMapping("/{filmId}/like/{userId}")
    public void unlike(@PathVariable Long filmId, @PathVariable Long userId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(),
                request.getRequestURI());
        filmService.unlikeFilm(filmId, userId);
        log.info("Пользователь: {} удалил лайк у фильма: {}", userId, filmId);
    }

    //TODO дописать доку
    @GetMapping("/popular")
    public List<Film> getFilmsSortedByTopLikes(@RequestParam(name = "count", defaultValue = "10", required = false)
                                                   Integer count, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(),
                request.getRequestURI());
        List<Film> filmsList = filmService.getPopular(count);
        log.info("В списке filmsList содержится {} фильмов", filmsList.size());
        return filmsList;
    }
}