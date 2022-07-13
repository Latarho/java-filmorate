package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private Film film;

    @BeforeEach
    public void BeforeEach() {
        film = Film.builder()
                .id(1L)
                .name("Чебурашка в поиске друзей")
                .description("Маленький коричневый ищет друзей")
                .releaseDate(LocalDate.of(1991, Month.OCTOBER, 9))
                .duration(150L)
                .mpa(new MpaRating(1, "G"))
                .build();
    }

    @Test
    void checkAddFilm() throws DataNotFoundException {
        Film filmAdd = filmDbStorage.addFilm(film);
        int actualResult = filmDbStorage.getAllFilms().size();
        assertEquals(1, actualResult);
    }

    @Test
    void checkGetFilmById() throws DataNotFoundException {
        Film filmAdd = filmDbStorage.addFilm(film);
        Optional<Film> actualResult = filmDbStorage.getFilmById(1L);
        assertEquals(filmAdd.getId(), actualResult.get().getId());
        assertEquals(actualResult.get().getName(), filmAdd.getName());
        assertEquals(actualResult.get().getDescription(), filmAdd.getDescription());
        assertEquals(actualResult.get().getReleaseDate(), filmAdd.getReleaseDate());
        assertEquals(actualResult.get().getDuration(), filmAdd.getDuration());
    }

    @Test
    void checkGetAllFilms() throws DataNotFoundException {
        Film filmAdd = filmDbStorage.addFilm(film);
        List<Film> actualResult = filmDbStorage.getAllFilms();
        assertEquals(actualResult.get(0).getId(), filmAdd.getId());
    }

    @Test
    void checkUpdateFilm()throws DataNotFoundException {
        Film filmAdd = filmDbStorage.addFilm(film);
        Film filmUp = Film.builder()
                .id(1L)
                .name("Чебурашка не в поиске друзей")
                .description("Маленький рыжий не ищет друзей")
                .releaseDate(LocalDate.of(1992, Month.OCTOBER, 9))
                .duration(120L)
                .mpa(new MpaRating(2, "PG"))
                .build();
        Film filmUpdate = filmDbStorage.updateFilm(filmUp);
        Optional<Film> actualResult = filmDbStorage.getFilmById(1L);
        assertEquals(actualResult.get().getId(), filmUpdate.getId());
        assertEquals(actualResult.get().getName(), filmUpdate.getName());
        assertEquals(actualResult.get().getDescription(), filmUpdate.getDescription());
        assertEquals(actualResult.get().getReleaseDate(), filmUpdate.getReleaseDate());
        assertEquals(actualResult.get().getDuration(), filmUpdate.getDuration());
    }

    @Test
    void checkDeleteById() throws DataNotFoundException {
        Film filmAdd = filmDbStorage.addFilm(film);
        int actualResultBeforeDeleting = filmDbStorage.getAllFilms().size();
        assertEquals(actualResultBeforeDeleting, 1);

        filmDbStorage.deleteById(filmAdd.getId());
        int actualResultAfterDeleting = filmDbStorage.getAllFilms().size();
        assertEquals(actualResultAfterDeleting, 0);
    }

    @Test
    void checkLikeFilm() throws DataNotFoundException {
        Film filmAdd = filmDbStorage.addFilm(film);
        User userCreate = userDbStorage.createUser(User.builder()
                .id(1L)
                .email("latarho@gmail.com")
                .login("Latarho")
                .name("Serg")
                .birthday(LocalDate.of(1991, Month.OCTOBER, 9))
                .build());
        Long actualResultBeforeLike = filmDbStorage.getFilmById(filmAdd.getId()).get().getRate();
        assertEquals(actualResultBeforeLike, 0);

        filmDbStorage.likeFilm(filmAdd.getId(), userCreate.getId());
        Long actualResultAfterLike = filmDbStorage.getFilmById(filmAdd.getId()).get().getRate();
        assertEquals(actualResultAfterLike, 1);
    }

    @Test
    void checkUnlikeFilm() throws DataNotFoundException {
        Film filmAdd = filmDbStorage.addFilm(film);
        User userCreate = userDbStorage.createUser(User.builder()
                .id(1L)
                .email("latarho@gmail.com")
                .login("Latarho")
                .name("Serg")
                .birthday(LocalDate.of(1991, Month.OCTOBER, 9))
                .build());
        filmDbStorage.likeFilm(filmAdd.getId(), userCreate.getId());
        Long actualResultBeforeUnlike = filmDbStorage.getFilmById(filmAdd.getId()).get().getRate();
        assertEquals(actualResultBeforeUnlike, 1);

        filmDbStorage.unlikeFilm(filmAdd.getId(), userCreate.getId());
        Long actualResultAfterUnlike = filmDbStorage.getFilmById(filmAdd.getId()).get().getRate();
        assertEquals(actualResultAfterUnlike, 0);
    }

    @Test
    void checkGetPopular() {
        Film filmOneAdd = filmDbStorage.addFilm(film);
        Film filmTwoAdd = filmDbStorage.addFilm(Film.builder()
                .id(2L)
                .name("Чебурашка не в поиске друзей")
                .description("Маленький рыжий не ищет друзей")
                .releaseDate(LocalDate.of(1992, Month.OCTOBER, 9))
                .duration(120L)
                .mpa(new MpaRating(2, "PG"))
                .build());
        User userCreate = userDbStorage.createUser(User.builder()
                .id(1L)
                .email("latarho@gmail.com")
                .login("Latarho")
                .name("Serg")
                .birthday(LocalDate.of(1991, Month.OCTOBER, 9))
                .build());
        filmDbStorage.likeFilm(filmOneAdd.getId(), userCreate.getId());

        Collection<Film> popularFilms = filmDbStorage.getPopular(10);
        assertEquals(popularFilms.stream().findFirst().get(), filmDbStorage.getFilmById(filmOneAdd.getId()).get());
    }
}