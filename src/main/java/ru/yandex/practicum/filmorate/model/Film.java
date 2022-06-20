package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {

    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private final Set<Long> likes = new HashSet<>();

    public void like(Long id) {
        likes.add(id);
    }
    public void unlike(Long id) {
        likes.remove(id);
    }
    public void setLikes(Set<Long> likes) {
        this.likes.addAll(likes);
    }
}