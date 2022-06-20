package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Аннотация указывает, что класс нужно добавить в контекст
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    private Long id = 1L;

    // Сообщаем Spring, что нужно передать в конструктор объект класса UserStorage
    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(FilmDto filmDto) {
        if (validationFilm(filmDto)) {
            LocalDate filmDtoReleaseDate = LocalDate.parse(filmDto.getReleaseDate());
            String filmName = filmDto.getName();
            for (Film film : filmStorage.getAllFilms()) {
                if (film.getName().equals(filmDto.getName()) && film.getReleaseDate().equals(filmDtoReleaseDate)) {
                    throw new ValidationException("Такой фильм уже содержится в базе.");
                }
                if (film.getName().equals(filmDto.getName())){
                    filmName = filmDto.getName() + " (" + filmDtoReleaseDate.getYear() + ")";
                }
            }

            Film film = Film.builder()
                    .id(generateId())
                    .name(filmName)
                    .description(filmDto.getDescription())
                    .releaseDate(filmDtoReleaseDate)
                    .duration(filmDto.getDuration())
                    .build();
            filmStorage.addFilm(film);
            return film;
        } else
            return null;
    }

    public Film getFilmById(Long id) {
        Optional<Film> film = filmStorage.getFilmById(id);
        if (film.isPresent())
            return film.get();
        throw new DataNotFoundException("Фильм c Id: " + id + "не найден.");
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film updateFilm(FilmDto filmDto) {
        if (filmDto.getId() != null && validationFilm(filmDto)) {
            Optional<Film> filmFromStorage = filmStorage.getFilmById(filmDto.getId());
            if (filmFromStorage.isPresent()) {
                LocalDate filmDtoReleaseDay = LocalDate.parse(filmDto.getReleaseDate());
                String name = filmDto.getName();
                for (Film film : filmStorage.getAllFilms()) {
                    if (film.getName().equals(name) && film.getReleaseDate().equals(filmDtoReleaseDay)) {
                        if (!film.getId().equals(filmFromStorage.get().getId()))
                            throw new ValidationException("Такой фильм уже содержится в базе.");
                    }
                }

                Film film = Film.builder()
                        .id(filmDto.getId())
                        .name(name)
                        .description(filmDto.getDescription())
                        .releaseDate(filmDtoReleaseDay)
                        .duration(filmDto.getDuration())
                        .build();
                film.setLikes(filmFromStorage.get().getLikes());
                filmStorage.addFilm(film);
                return film;
            } else
                throw new DataNotFoundException("Фильм: " + filmDto + "не найден.");
        } else {
            throw new ValidationException("Валидация не пройдена или указан некорректный Id.");
        }
    }

    public void likeFilm(Long filmId, Long userId) {
        if (filmId <= 0 || userId <= 0) {
            throw new ValidationException("Передан некорректный Id фильма.");
        }

        Optional<Film> filmFromStorage = filmStorage.getFilmById(filmId);
        User user = userService.getUserById(userId);
        if (filmFromStorage.isPresent()) {
            Film film = filmFromStorage.get();
            film.like(user.getId());
            filmStorage.addFilm(film);
        } else
            throw new DataNotFoundException("Фильм не найден.");
    }

    public void unlikeFilm(Long filmId, Long userId) {
        if (filmId <= 0 || userId <= 0) {
            // В тестах постман здесь ожидается статус код 404, однако по факту эта операция соответсвует 400 коду, следовательно здесь должен быть ValidationException.
            // Переделать тесты в постмане.
            throw new DataNotFoundException("Передан некорректный Id фильма.");
        }

        Optional<Film> filmFromStorage = filmStorage.getFilmById(filmId);
        User user = userService.getUserById(userId);
        if (filmFromStorage.isPresent()) {
            Film film = filmFromStorage.get();
            film.unlike(user.getId());
            filmStorage.addFilm(film);
        } else
            throw new DataNotFoundException("Фильм не найден.");
    }

    public List<Film> getPopular(Integer count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> -Integer.compare(f1.getLikes().size(), f2.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Валидация экземпляра класса Film.
     *
     * @param filmDto объект класса Film (из тела запроса).
     * @return true or false результат валидации.
     */
    public boolean validationFilm(FilmDto filmDto) {
        LocalDate date = LocalDate.of(1895, 12, 28);
        LocalDate filmDtoReleaseDay = LocalDate.parse(filmDto.getReleaseDate());
        if (filmDtoReleaseDay.isBefore(date)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 января 1895 года");
        }
        if (filmDto.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
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