package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Component("genreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_GET_GENRE_BY_ID = "SELECT * FROM GENRES WHERE ID=?";
    private static final String SQL_GET_ALL_GENRES = "SELECT * FROM GENRES";
    private static final String SQL_ADD_GENRE_FOR_FILM = "INSERT INTO FILM_GENRE(film_id, genre_id) VALUES (?,?)";
    private static final String SQL_DELETE_GENRE_FOR_FILM = "DELETE FROM FILM_GENRE WHERE FILM_ID=?";

    @Autowired
    public GenreDbStorage (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(int id) {
        try {
            return jdbcTemplate.queryForObject(SQL_GET_GENRE_BY_ID,
                    (rs, rowNum) -> new Genre(rs.getInt("ID"), rs.getString("NAME")), id);
        } catch (DataAccessException exc) {
            throw new DataNotFoundException("Жанра с таким ID нет в базе.");
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query(SQL_GET_ALL_GENRES, (rs, rowNum) -> new Genre(rs.getInt("ID"),
                rs.getString("NAME")));
    }

    @Override
    public void addGenreForFilm(Long filmId, Integer genreId) {
        jdbcTemplate.update(SQL_ADD_GENRE_FOR_FILM, filmId, genreId);
    }

    @Override
    public void deleteGenreForFilm(Long filmId) {
        jdbcTemplate.update(SQL_DELETE_GENRE_FOR_FILM, filmId);
    }
}