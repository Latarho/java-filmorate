package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

@Component("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_GET_MPA_BY_ID = "SELECT * FROM MPA_RATING WHERE ID=?";
    private static final String SQL_GET_ALL_MPA = "SELECT * FROM MPA_RATING";

    @Autowired
    public MpaDbStorage (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MpaRating getMpaRatingById(int id) {
        try {
            return jdbcTemplate.queryForObject(SQL_GET_MPA_BY_ID,
                    (rs, rowNum) -> new MpaRating(rs.getInt("ID"), rs.getString("NAME")), id);
        } catch (DataAccessException exc) {
            throw new DataNotFoundException("Такого рейтинга нет в базе данных.");
        }
    }

    @Override
    public Collection<MpaRating> getAllMpa() {
        return jdbcTemplate.query(SQL_GET_ALL_MPA, (rs, rowNum) -> new MpaRating(rs.getInt("ID"),
                rs.getString("NAME")));
    }
}