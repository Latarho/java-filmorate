package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UserDto {

    private final Long id;

    @NotBlank(message = "Поле email не может быть пустым")
    @Email(message = "Некорректный формат электронной почты")
    private final String email;

    @NotBlank(message = "Поле login не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Login не может содержать пробел")
    private final String login;

    @NotNull(message = "Поле name может быть пустое, но не может быть null")
    private final String name;

    private final String birthday;
}