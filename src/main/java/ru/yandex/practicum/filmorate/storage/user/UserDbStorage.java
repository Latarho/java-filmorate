package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

//TODO Добавить логирование
//TODO Текст в датанотфаунд экс
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO USERS (ID, EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(
                sql,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );

        return jdbcTemplate.queryForObject(
                "SELECT * FROM USERS WHERE ID = " + user.getId(),
                (rs, rowNum) -> makeUser(rs)
        );
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String sql = "SELECT * FROM USERS WHERE ID=?";
        try {
            User user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
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
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE ID=?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return getUserById(user.getId()).get();
    }

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