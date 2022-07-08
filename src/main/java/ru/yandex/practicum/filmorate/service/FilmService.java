package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

// Аннотация указывает, что класс нужно добавить в контекст
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    private Long id = 1L;

    // Сообщаем Spring, что нужно передать в конструктор объект класса UserStorage
    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        if (validationFilm(film)) {
            LocalDate filmReleaseDate = film.getReleaseDate();
            String filmName = film.getName();
            for (Film fs : filmStorage.getAllFilms()) {
                if (fs.getName().equals(film.getName()) && fs.getReleaseDate().equals(filmReleaseDate)) {
                    throw new ValidationException("Такой фильм уже содержится в базе.");
                }
                if (fs.getName().equals(film.getName())){
                    filmName = film.getName() + " (" + filmReleaseDate.getYear() + ")";
                }
            }

            Film filmAdd = Film.builder()
                    .id(generateId())
                    .name(filmName)
                    .description(film.getDescription())
                    .releaseDate(filmReleaseDate)
                    .duration(film.getDuration())
                    .rate(film.getRate())
                    .mpa(filmStorage.getMpaRatingById(film.getMpa().getId()))
                    .genres(createGenreList(film))
                    .build();
            filmStorage.addFilm(filmAdd);
            return filmAdd;
        } else
            return null;
    }

    public Film getFilmById(Long id) {
        Optional<Film> film = filmStorage.getFilmById(id);
        return film.orElseThrow(() -> new DataNotFoundException("Фильм c Id: " + id + "не найден."));
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film updateFilm(Film film) {
        if (film.getId() != null && validationFilm(film)) {
            Optional<Film> filmFromStorage = filmStorage.getFilmById(film.getId());
            if (filmFromStorage.isPresent()) {
                LocalDate filmReleaseDay = film.getReleaseDate();
                String filmName = film.getName();
                for (Film fs : filmStorage.getAllFilms()) {
                    if (fs.getName().equals(filmName) && fs.getReleaseDate().equals(filmReleaseDay)) {
                        if (!fs.getId().equals(filmFromStorage.get().getId()))
                            throw new ValidationException("Такой фильм уже содержится в базе.");
                    }
                }

                Film filmEdit = Film.builder()
                        .id(film.getId())
                        .name(filmName)
                        .description(film.getDescription())
                        .releaseDate(filmReleaseDay)
                        .duration(film.getDuration())
                        .rate(film.getRate())
                        .mpa(filmStorage.getMpaRatingById(film.getMpa().getId()))
                        .genres(createGenreList(film))
                        .build();
                return filmStorage.updateFilm(filmEdit);
            } else
                throw new DataNotFoundException("Фильм: " + film + "не найден.");
        } else {
            throw new ValidationException("Валидация не пройдена или указан некорректный Id.");
        }
    }

    public void likeFilm(Long filmId, Long userId) {
        Optional<Film> filmFromStorage = filmStorage.getFilmById(filmId);
        userService.getUserById(userId);
        if (filmFromStorage.isPresent()) {
            filmStorage.likeFilm(filmId, userId);
        } else
            throw new DataNotFoundException("Фильм не найден.");
    }

    public void unlikeFilm(Long filmId, Long userId) {
        Optional<Film> filmFromStorage = filmStorage.getFilmById(filmId);
        userService.getUserById(userId);
        if (filmFromStorage.isPresent()) {
            filmStorage.unlikeFilm(filmId, userId);
        } else throw new DataNotFoundException("Фильм не найден.");
    }

    public Collection<Film> getPopular(Integer count) {
        return filmStorage.getPopular(count);
    }

    private HashSet<Genre> createGenreList(Film film) {
        if (film.getGenres() == null) return null;
        if (film.getGenres().isEmpty()) return new HashSet<>();
        HashSet<Genre> genres = new HashSet<>();
        for (Genre g : film.getGenres()) {
            genres.add(filmStorage.getGenreById(g.getId()));
        }
        return genres;
    }

    /**
     * Валидация экземпляра класса Film.
     *
     * @param film объект класса Film (из тела запроса).
     * @return true or false результат валидации.
     */
    public boolean validationFilm(Film film) {
        LocalDate date = LocalDate.of(1895, 12, 28);
        LocalDate filmReleaseDay = film.getReleaseDate();
        if (filmReleaseDay.isBefore(date)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 января 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getMpa() == null) {
            throw new ValidationException("У фильма должно быть заполнено поле Mpa");
        }
        return true;
    }

    /**
     * Генерация уникального Id фильма.
     *
     * @return Id фильма.
     */
    private Long generateId() {
        return id++;
    }
}