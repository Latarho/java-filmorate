//package ru.yandex.practicum.filmorate.storage;
//
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.filmorate.model.Film;
//
//import java.util.*;
//
//@Component
//public class InMemoryFilmStorage implements FilmStorage {
//
//    private final Map<Long, Film> films = new HashMap<>();
//
//    @Override
//    public Film addFilm(Film film) {
//        films.put(film.getId(), film);
//        return film;
//    }
//
//    @Override
//    public Optional<Film> getFilmById(Long id) {
//        return Optional.ofNullable(films.get(id));
//    }
//
//    @Override
//    public List<Film> getAllFilms() {
//        return new ArrayList<>(films.values());
//    }
//}