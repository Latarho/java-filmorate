package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.validator.constraints.time.DurationMin;

import javax.validation.ValidationException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Film {

    private int id;

    @NotBlank(message = "Поле name не может быть пустым")
    private String name;

    @Size(min = 1, max = 200, message = "Количество знаков в description должно быть в диапазоне от 1 до 200 знаков")
    @NotBlank (message = "Поле description не может быть пустым")
    private String description;

    @NotNull(message = "Поле releaseDate может быть пустое, но не может быть null")
    private LocalDate releaseDate;

    @NotNull(message = "Поле duration может быть пустое, но не может быть null")
    @EqualsAndHashCode.Exclude
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;

    public Film (String name, String description, LocalDate releaseDate, Duration duration) {
        validation(releaseDate, duration);
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    private void validation(LocalDate releaseDate, Duration duration) {
        LocalDate date = LocalDate.of(1895, 12, 28);
        if (releaseDate.isBefore(date)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 января 1895 года");
        }
        if (duration.isNegative() || duration.isZero()) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}