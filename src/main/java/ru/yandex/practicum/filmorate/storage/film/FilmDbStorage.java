package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import javax.validation.ValidationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    private static final String SQL_ADD_FILM =
            "INSERT INTO FILMS(ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_RATING_ID) VALUES (?,?,?,?,?,?,?)";
    private static final String SQL_GET_FILM_BY_ID = "SELECT * FROM FILMS WHERE ID=?";
    private static final String SQL_GET_ALL_FILMS = "SELECT * FROM FILMS";
    private static final String SQL_UPDATE_FILM = "MERGE INTO FILMS key (ID) VALUES (?,?,?,?,?,?,?)";
    private static final String SQL_ORDER_BY_RATE = "SELECT * FROM FILMS ORDER BY RATE DESC LIMIT ?";
    private static final String SQL_SET_RATE = "UPDATE FILMS SET RATE=? WHERE ID=?";
    private static final String SQL_DELETE_FILM = "DELETE FROM FILMS WHERE ID=?";
    //TODO сделать отдельный класс с методами
    private static final String SQL_GET_FILM_GENRE = "SELECT * FROM FILM_GENRE WHERE FILM_ID=?";
    private static final String SQL_GET_USER_ID_FROM_FILM_LIKES =
            "SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID=? AND USER_ID=?";
    private static final String SQL_ADD_FILM_INTO_FILM_LIKES = "insert into FILM_LIKES(FILM_ID, USER_ID) VALUES (?,?)";
    private static final String SQL_DELETE_LIKES = "DELETE FROM FILM_LIKES WHERE FILM_ID=? AND USER_ID=?";

    @Autowired
    public FilmDbStorage (JdbcTemplate jdbcTemplate, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Film addFilm(Film film) {
        jdbcTemplate.update(SQL_ADD_FILM,
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRate(), film.getMpa().getId());

        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                genreStorage.addGenreForFilm(film.getId(), g.getId());
            }
        }
        return getFilmById(film.getId()).get();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        Film film;
        List<Genre> genres;
        try {
            film = jdbcTemplate.queryForObject(SQL_GET_FILM_BY_ID, (rs, rowNum) -> makeFilm(rs), id);
        } catch (DataAccessException exc) {
            return Optional.empty();
        }
        try {
            genres = jdbcTemplate.query(SQL_GET_FILM_GENRE, (rs, rowNum) ->
                            genreStorage.getGenreById(rs.getInt("GENRE_ID")), id)
                    .stream().sorted(Comparator.comparingLong(Genre::getId)).collect(Collectors.toList());
            assert film != null;
            if (!genres.isEmpty()) {
                HashSet<Genre> genre = new HashSet<>();
                genre.addAll(genres);
                film.setGenres(genre);
            } else {
                film.setGenres(null);
            }
        } catch (DataAccessException exc) {
            assert film != null;
            film.setGenres(new HashSet<>());
        }
        return Optional.of(film);
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(SQL_GET_ALL_FILMS, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(SQL_UPDATE_FILM,
                film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRate(), film.getMpa().getId());

        genreStorage.deleteGenreForFilm(film.getId());
        if (film.getGenres() != null) {
            if (film.getGenres().isEmpty()) {
                return film;
            }
            for (Genre g : film.getGenres()) {
                genreStorage.addGenreForFilm(film.getId(), g.getId());
            }
        }
        return getFilmById(film.getId()).get();
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(SQL_DELETE_FILM, id);
    }

    @Override
    public void likeFilm(Long filmId, Long userId) {
        try {
            jdbcTemplate.queryForObject(SQL_GET_USER_ID_FROM_FILM_LIKES, Long.class, filmId, userId);
            throw new ValidationException("Этот пользователь уже поставил лайк этому фильму");
        } catch (IncorrectResultSizeDataAccessException e) {
            jdbcTemplate.update(SQL_ADD_FILM_INTO_FILM_LIKES, filmId, userId);
            jdbcTemplate.update(SQL_SET_RATE, getFilmById(filmId).get().getRate() + 1, filmId);
        }
    }

    @Override
    public void unlikeFilm(Long filmId, Long userId) {
        jdbcTemplate.update(SQL_DELETE_LIKES, filmId, userId);
        jdbcTemplate.update(SQL_SET_RATE, getFilmById(filmId).get().getRate() - 1, filmId);
    }

    @Override
    public Collection<Film> getPopular(Integer count) {
        return jdbcTemplate.query(SQL_ORDER_BY_RATE, (rs, rowNum) -> makeFilm(rs), count);
    }

    /**
     * Маппинг объекта Film.
     * @param rs строка из БД.
     * @return объект класса Film.
     * @throws SQLException исключение.
     */
    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .rate(rs.getLong("rate"))
                .mpa(mpaStorage.getMpaRatingById(rs.getInt("mpa_rating_id")))
                .build();
    }
}