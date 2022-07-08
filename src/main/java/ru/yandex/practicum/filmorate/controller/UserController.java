package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создание нового пользователя.
     *
     * @param user объект класса User (из тела запроса).
     * @param request запрос.
     * @return объект класса User.
     */
    @PostMapping
    public User createUser(@Valid @RequestBody User user, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                request.getRequestURI(), user);
        User newUser = userService.createUser(user);
        log.info("Пользователь: {} успешно создан", user.getLogin());
        return newUser;
    }

    /**
     * Получение значения (объект класса User) по переданному id.
     *
     * @param id идентификатор User.
     * @param request запрос.
     * @return объект класса User соответствующее переданному id.
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(),
                request.getRequestURI());
        User user = userService.getUserById(id);
        log.info("Пользователь: {} успешно найден", user.getLogin());
        return user;
    }

    /**
     * Получение списка пользователей.
     *
     * @return список пользователей.
     */
    @GetMapping
    public List<User> getAllUsers(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(),
                request.getRequestURI());
        List<User> usersList = userService.getAllUsers();
        log.info("В списке usersList содержится {} пользователей", usersList.size());
        return usersList;
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
        log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                    request.getRequestURI(), user);
        User upUser = userService.updateUser(user);
        log.info("Пользователь: {} успешно обновлен", user);
        return upUser;
    }

    //TODO дописать доку
    @PutMapping("{userId}/friends/{friendId}")
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(),
                request.getRequestURI());
        userService.addFriend(userId, friendId);
        log.info("Пользователь: {} успешно добавил в друзья пользователя: {}", userId, friendId);
    }

    //TODO дописать доку
    @DeleteMapping("{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId, @PathVariable Long friendId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(),
                request.getRequestURI());
        userService.deleteFriend(userId, friendId);
        log.info("Пользователь: {} успешно удален", userId);
    }

    //TODO дописать доку
    @GetMapping("{userId}/friends")
    public List<User> findFriends(@PathVariable Long userId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(),
                request.getRequestURI());
        List<User> friendsList = userService.getUserFriends(userId);
        log.info("В списке friendsList содержится {} пользователей", friendsList.size());
        return friendsList;
    }

    //TODO дописать доку
    @GetMapping("{userId}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable Long userId, @PathVariable Long otherId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}", request.getMethod(),
                request.getRequestURI());
        List<User> commonFriendsList = userService.getCommonFriends(userId, otherId);
        log.info("В списке commonFriendsList содержится {} пользователей", commonFriendsList.size());
        return commonFriendsList;
    }
}