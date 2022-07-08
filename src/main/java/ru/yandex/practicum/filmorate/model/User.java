package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {

    private final Long id;

    @NotBlank(message = "Поле email не может быть пустым")
    @Email(message = "Некорректный формат электронной почты")
    private final String email;

    @NotBlank(message = "Поле login не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Login не может содержать пробел")
    private final String login;

    @NotNull(message = "Поле name может быть пустое, но не может быть null")
    private String name;

    private final LocalDate birthday;
}