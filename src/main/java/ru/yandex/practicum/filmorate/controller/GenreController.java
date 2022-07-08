package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    /**
     * Получение значения (объект класса Genre) по переданному id.
     *
     * @param id идентификатор Genre.
     * @param request запрос.
     * @return объект класса Genre соответствующее переданному id.
     */
    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(), request.getRequestURI());
        Genre genre = genreService.getGenreById(id);
        log.info("Жанр фильма успешно найден: {}", genre.getName());
        return genre;
    }

    /**
     * Получение списка жанров фильмов.
     *
     * @return список жанров фильмов.
     */
    @GetMapping
    public Collection<Genre> getAllGenre(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(), request.getRequestURI());
        Collection<Genre> genreList = genreService.getAllGenres();
        log.info("В списке genreList содержится фильмов: {}", genreList.size());
        return genreList;
    }
}