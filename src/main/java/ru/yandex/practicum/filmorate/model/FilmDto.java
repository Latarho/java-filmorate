package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class FilmDto {

    private final Long id;

    @NotBlank(message = "Поле name не может быть пустым")
    private final String name;

    @Size(min = 1, max = 200, message = "Количество знаков в description должно быть в диапазоне от 1 до 200 знаков")
    @NotBlank (message = "Поле description не может быть пустым")
    private final String description;

    @NotNull(message = "Поле releaseDate может быть пустое, но не может быть null")
    private final String releaseDate;;

    @NotNull(message = "Поле duration может быть пустое, но не может быть null")
    private final Long duration;
}