package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_CREATE_USER =
            "INSERT INTO USERS (ID, EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?,?,?,?,?)";
    private static final String SQL_GET_USER_BY_ID = "SELECT * FROM USERS WHERE ID=?";
    private static final String SQL_GET_ALL_USERS = "SELECT * FROM USERS";
    private static final String SQL_UPDATE_USER = "UPDATE USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE ID=?";
    private static final String SQL_DELETE_USER = "DELETE FROM USERS WHERE ID=?";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        //Делаем запись в БД
        jdbcTemplate.update(SQL_CREATE_USER,
                user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return getUserById(user.getId()).get();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        try {
            User user = jdbcTemplate.queryForObject(SQL_GET_USER_BY_ID, (rs, rowNum) -> makeUser(rs), id);
            if (user == null) {
                return Optional.empty();
            } else {
                return Optional.of(user);
            }
        } catch (DataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query(SQL_GET_ALL_USERS, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(SQL_UPDATE_USER,
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return getUserById(user.getId()).get();
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(SQL_DELETE_USER, id);
    }

    /**
     * Маппинг объекта User.
     * @param rs строка из БД.
     * @return объект класса User.
     * @throws SQLException исключение.
     */
    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}