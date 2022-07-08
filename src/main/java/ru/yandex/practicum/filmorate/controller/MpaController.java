package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    /**
     * Получение значения (объект класса Mpa) по переданному id.
     *
     * @param id идентификатор Mpa.
     * @param request запрос.
     * @return объект класса Mpa соответствующее переданному id.
     */
    @GetMapping("/{id}")
    public MpaRating getMpaRatingById(@PathVariable int id, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(), request.getRequestURI());
        MpaRating mpaRating = mpaService.getMpaRatingById(id);
        log.info("Рейтинг успешно найден: {}", mpaRating.getName());
        return mpaRating;
    }

    /**
     * Получение списка рейтингов.
     *
     * @return список рейтингов.
     */
    @GetMapping
    public Collection<MpaRating> getAllMpa(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(), request.getRequestURI());
        Collection<MpaRating> mpaRatingList = mpaService.getAllMpa();
        log.info("В списке mpaRatingList содержится фильмов: {}", mpaRatingList.size());
        return mpaRatingList;
    }
}