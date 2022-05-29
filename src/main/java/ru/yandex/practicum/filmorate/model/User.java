package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User {

    private int id;

    @NotBlank(message = "Поле email не может быть пустым")
    @Email(message = "Некорректный формат электронной почты")
    private String email;

    @NotBlank(message = "Поле login не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Login не может содрежать пробел")
    private String login;

    @NotNull(message = "Поле name может быть пустое, но не может быть null")
    private String name;

    @Past(message = "Дата рождения - прошлое время")
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        if (name.isBlank()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
    }
}