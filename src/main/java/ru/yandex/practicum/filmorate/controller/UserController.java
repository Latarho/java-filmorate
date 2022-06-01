package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    /**
     * Создание нового пользователя.
     *
     * @param user объект класса User (из тела запроса).
     * @param request запрос.
     * @return объект класса User.
     */
    @PostMapping
    public User createUser(@Valid @RequestBody User user, HttpServletRequest request) {
        try {
            log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                    request.getRequestURI(), user);
            validationUser(user);
            user.setId(generateId());
            users.put(user.getId(), user);
            log.info("Пользователь: {} успешно создан", user);
        } catch (ValidationException exc) {
            log.info("Произошла ошибка валидации: " + exc.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exc.getMessage());
        }
        return user;
    }

    /**
     * Обновление существующего пользователя.
     *
     * @param user объект класса User (из тела запроса).
     * @param request запрос.
     * @return объект класса User.
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user, HttpServletRequest request) {
        try {
            log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                    request.getRequestURI(), user);
            if (!users.containsKey(user.getId())) {
                log.info("Пользователь: {} не найден", user);
                throw new UserNotFoundException("Пользователь: " + user + "не найден");
            } else {
                validationUser(user);
                users.put(user.getId(), user);
                log.info("Пользователь: {} успешно обновлен", user);
            }
        } catch (ValidationException exc) {
            log.info("Произошла ошибка валидации: " + exc.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exc.getMessage());
        }
        return user;
    }

    /**
     * Получение списка пользователей.
     *
     * @return список пользователей.
     */
    @GetMapping
    public ArrayList<User> getAllUsers() {
        ArrayList<User> usersList = new ArrayList<>();
        usersList.addAll(users.values());
        log.info("В списке usersList содержится {} пользователей", usersList.size());
        return usersList;
    }

    protected void validationUser(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения пользователя должна быть раньше текущей даты");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    /**
     * Генерация уникального Id пользователя.
     *
     * @return Id пользователя.
     */
    private int generateId() {
        return id++;
    }
}