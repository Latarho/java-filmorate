package ru.yandex.practicum.filmorate.storage.userFriendship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriendship;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//TODO Добавить логирование
//TODO Текст в датанотфаунд экс
@Component("userFriendshipDbStorage")
public class UserFriendshipDbStorage implements UserFriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserFriendshipDbStorage (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        Optional<UserFriendship> uf = getUserFriendship(userId, friendId);
        if (uf.isEmpty()) {
            jdbcTemplate.update(
                    "INSERT INTO USER_FRIENDSHIP(FIRST_USER_ID, SECOND_USER_ID, STATUS) VALUES (?,?,?)",
                    userId, friendId, false);
            return;
        }
        UserFriendship userFriendship = uf.get();
        if (userFriendship.getFirstUserId().equals(friendId)) {
            if (!userFriendship.isStatus()) {
                jdbcTemplate.update(
                        "UPDATE USER_FRIENDSHIP SET STATUS=true WHERE FIRST_USER_ID=? AND SECOND_USER_ID=?",
                        friendId, userId);
            } else {
                throw new DuplicateKeyException("Эти пользователи уже друзья.");
            }
        } else {
            throw new DuplicateKeyException("Запрос невозможно выполнить.");
        }
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        Optional<UserFriendship> uf = getUserFriendship(userId, friendId);
        if (uf.isEmpty()) {
            throw new DataNotFoundException("Эти пользователи не были друзьями.");
        }
        UserFriendship userFriendship = uf.get();
        jdbcTemplate.update(
                "DELETE FROM USER_FRIENDSHIP WHERE FIRST_USER_ID=? and SECOND_USER_ID=?",
                userFriendship.getFirstUserId(), userFriendship.getSecondUserId());
    }

    @Override
    public List<Long> getUserFriends(Long userId) {
        String sql = "SELECT * FROM USER_FRIENDSHIP WHERE FIRST_USER_ID=? or SECOND_USER_ID=? and STATUS=true";
        List<UserFriendship> userFriendship = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> UserFriendship.builder()
                        .firstUserId(rs.getLong("FIRST_USER_ID"))
                        .secondUserId(rs.getLong("SECOND_USER_ID"))
                        .status(rs.getBoolean("STATUS")).build(),
                userId, userId);

        if (userFriendship.isEmpty()) {
            return List.of();
        }

        List<Long> userFriends = new ArrayList<>();

        for (UserFriendship uf : userFriendship) {
            if (uf.getFirstUserId().equals(userId)) userFriends.add(uf.getSecondUserId());
            else userFriends.add(uf.getFirstUserId());
        }
        return userFriends;
    }

    private Optional<UserFriendship> getUserFriendship(Long userId, Long friendId) {
        String sql = "SELECT * FROM USER_FRIENDSHIP WHERE FIRST_USER_ID IN (?,?) AND SECOND_USER_ID IN (?,?)";
        try {
            UserFriendship uf = jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> UserFriendship.builder()
                            .firstUserId(rs.getLong("FIRST_USER_ID"))
                            .secondUserId(rs.getLong("SECOND_USER_ID"))
                            .status(rs.getBoolean("STATUS")).build(),
                    userId, friendId, userId, friendId);
            return Optional.ofNullable(uf);
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }
}