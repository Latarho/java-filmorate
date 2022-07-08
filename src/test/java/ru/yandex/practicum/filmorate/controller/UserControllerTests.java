//package ru.yandex.practicum.filmorate.controller;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.model.UserDto;
//import ru.yandex.practicum.filmorate.service.UserService;
//import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
//
//import javax.validation.ValidationException;
//
//public class UserControllerTests {
//
//    UserService userService = new UserService(new InMemoryUserStorage());
//
//    @Test
//    void checkValidationExceptionBirthday() {
//        UserDto userOne = new UserDto(
//                null,
//                "dfkjgdf@gdfjklgd.com",
//                "dfkjgdf",
//                "dffgdghkjgdf",
//                "2031-09-10");
//        Assertions.assertThrows(ValidationException.class, () -> userService.validationUser(userOne));
//    }
//}