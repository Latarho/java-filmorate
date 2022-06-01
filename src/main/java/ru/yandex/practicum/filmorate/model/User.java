package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User {

    private Long id;

    @NotBlank(message = "Поле email не может быть пустым")
    @Email(message = "Некорректный формат электронной почты")
    private String email;

    @NotBlank(message = "Поле login не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Login не может содержать пробел")
    private String login;

    @NotNull(message = "Поле name может быть пустое, но не может быть null")
    private String name;

    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}