package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaStorage {

    MpaRating getMpaRatingById(int id);

    Collection<MpaRating> getAllMpa();
}