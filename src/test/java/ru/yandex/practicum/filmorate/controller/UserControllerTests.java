package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ValidationException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTests {

    UserController userController = new UserController();

    @Test
    void checkValidationExceptionBirthday() {
        User userOne = new User("dfkjgdf@gdfjklgd.com", "dfkjgdf", "dfkjgdf",
                LocalDate.of(2030, 11, 28));
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.validationUser(userOne));
        assertEquals("Дата рождения пользователя должна быть раньше текущей даты", exception.getMessage());
    }
}