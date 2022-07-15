package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;

@Data
@Builder
public class Film {

    private Long id;

    @NotBlank(message = "Поле name не может быть пустым")
    private String name;

    @Size(min = 1, max = 200, message = "Количество знаков в description должно быть в диапазоне от 1 до 200 знаков")
    @NotBlank(message = "Поле description не может быть пустым")
    private String description;

    @NotNull(message = "Поле releaseDate может быть пустое, но не может быть null")
    private LocalDate releaseDate;

    @NotNull(message = "Поле duration может быть пустое, но не может быть null")
    private Long duration;

    private Long rate;

    private MpaRating mpa;

    private HashSet<Genre> genres;
}