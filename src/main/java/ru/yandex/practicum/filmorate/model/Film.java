package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}