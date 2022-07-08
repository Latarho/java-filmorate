package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import javax.validation.ValidationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

//TODO Добавить логирование
//TODO Текст в датанотфаунд экс
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO FILMS(ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_RATING_ID)" +
                "VALUES (?,?,?,?,?,?,?)";
        jdbcTemplate.update(
                sql,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId()
        );

        if (film.getGenres() != null) {
            String genreSql = "insert into FILM_GENRE(film_id, genre_id) values (?,?)";
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(genreSql, film.getId(), g.getId());
            }
        }
        return getFilmById(film.getId()).get();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        String sql = "select * from FILMS where ID=?";
        String genreSql = "select * from FILM_GENRE where FILM_ID=?";
        Film film;
        List<Genre> genres;
        try {
            film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id);
        } catch (DataAccessException exc) {
            return Optional.empty();
        }
        try {
            genres = jdbcTemplate.query(
                    genreSql,
                    (rs, rowNum) -> getGenreById(rs.getInt("genre_id")),
                    id).stream().sorted(Comparator.comparingLong(Genre::getId)).collect(Collectors.toList());
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
        String sql = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "merge into FILMS key (ID) values (?,?,?,?,?,?,?)";
        jdbcTemplate.update(
                sql,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId()
        );

        jdbcTemplate.update("delete from FILM_GENRE where FILM_ID=?", film.getId());
        if (film.getGenres() != null) {
            if (film.getGenres().isEmpty()) {
                return film;
            }
            String genreSql = "insert into FILM_GENRE(film_id, genre_id) values (?,?)";
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(genreSql, film.getId(), g.getId());
            }
        }
        return getFilmById(film.getId()).get();
    }

    @Override
    public void likeFilm(Long filmId, Long userId) {
        try {
            jdbcTemplate.queryForObject(
                    "select USER_ID from FILM_LIKES where FILM_ID=? and USER_ID=?",
                    Long.class,
                    filmId,
                    userId
            );
            throw new ValidationException("Этот пользователь уже поставил лайк этому фильму");
        } catch (IncorrectResultSizeDataAccessException e) {
            jdbcTemplate.update("insert into FILM_LIKES(FILM_ID, USER_ID) VALUES (?,?)", filmId, userId);
            jdbcTemplate.update("update FILMS set RATE=? where ID=?",
                    getFilmById(filmId).get().getRate() + 1, filmId);
        }
    }

    @Override
    public void unlikeFilm(Long filmId, Long userId) {
        String sql = "delete from FILM_LIKES where FILM_ID=? and USER_ID=?";
        jdbcTemplate.update(sql, filmId, userId);
        jdbcTemplate.update("update FILMS set RATE=? where ID=?",
                getFilmById(filmId).get().getRate() - 1, filmId);
    }

    @Override
    public Collection<Film> getPopular(Integer count) {
        String sql = "select * from FILMS order by RATE desc limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public MpaRating getMpaRatingById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from MPA_RATING where ID=?",
                    (rs, rowNum) -> new MpaRating(rs.getInt("ID"), rs.getString("NAME")),
                    id);
        } catch (DataAccessException e) {
            throw new DataNotFoundException("Такого рейтинга нет в базе данных.");
        }
    }

    @Override
    public Collection<MpaRating> getAllMpa() {
        return jdbcTemplate.query(
                "select * from MPA_RATING",
                (rs, rowNum) -> new MpaRating(rs.getInt("ID"), rs.getString("NAME"))
        );
    }

    @Override
    public Genre getGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from GENRES where ID=?",
                    (rs, rowNum) -> new Genre(rs.getInt("ID"), rs.getString("NAME")),
                    id
            );
        } catch (DataAccessException e) {
            throw new DataNotFoundException("Жанра с таким ID нет в базе.");
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query(
                "select * from GENRES", (rs, rowNum) -> new Genre(rs.getInt("ID"),
                        rs.getString("NAME"))
        );
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .rate(rs.getLong("rate"))
                .mpa(getMpaRatingById(rs.getInt("mpa_rating_id")))
                .build();
    }
}