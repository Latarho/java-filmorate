package ru.yandex.practicum.filmorate.storage.userFriendship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.UserFriendship;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component("userFriendshipDbStorage")
@Slf4j
public class UserFriendshipDbStorage implements UserFriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_ADD_FRIEND =
            "INSERT INTO USER_FRIENDSHIP(FIRST_USER_ID, SECOND_USER_ID, STATUS) VALUES (?,?,?)";
    private static final String SQL_CHANGE_USER_FRIENDSHIP_STATUS =
            "UPDATE USER_FRIENDSHIP SET STATUS=true WHERE FIRST_USER_ID=? AND SECOND_USER_ID=?";
    private static final String SQL_DELETE_FRIEND =
            "DELETE FROM USER_FRIENDSHIP WHERE FIRST_USER_ID=? and SECOND_USER_ID=?";
    private static final String SQL_GET_USER_FRIENDS =
            "SELECT * FROM USER_FRIENDSHIP WHERE FIRST_USER_ID=? or SECOND_USER_ID=? and STATUS=true";
    private static final String SQL_GET_FRIENDSHIP =
            "SELECT * FROM USER_FRIENDSHIP WHERE FIRST_USER_ID IN (?,?) AND SECOND_USER_ID IN (?,?)";

    @Autowired
    public UserFriendshipDbStorage (JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        Optional<UserFriendship> uf = getUserFriendship(userId, friendId);
        if (uf.isEmpty()) {
            jdbcTemplate.update(SQL_ADD_FRIEND, userId, friendId, false);
            return;
        }
        if (uf.get().getFirstUserId().equals(friendId)) {
            if (!uf.get().isStatus()) {
                jdbcTemplate.update(SQL_CHANGE_USER_FRIENDSHIP_STATUS, friendId, userId);
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
        jdbcTemplate.update(SQL_DELETE_FRIEND, uf.get().getFirstUserId(), uf.get().getSecondUserId());
    }

    @Override
    public List<Long> getUserFriends(Long userId) {
        List<UserFriendship> userFriendship = jdbcTemplate.query(SQL_GET_USER_FRIENDS, (rs, rowNum) ->
                        UserFriendship.builder()
                        .firstUserId(rs.getLong("FIRST_USER_ID"))
                        .secondUserId(rs.getLong("SECOND_USER_ID"))
                        .status(rs.getBoolean("STATUS")).build(), userId, userId);

        if (userFriendship.isEmpty()) {
            return List.of();
        }

        List<Long> userFriends = new ArrayList<>();

        for (UserFriendship uf : userFriendship) {
            if (uf.getFirstUserId().equals(userId)) {
                userFriends.add(uf.getSecondUserId());
            } else userFriends.add(uf.getFirstUserId());
        }
        return userFriends;
    }

    private Optional<UserFriendship> getUserFriendship(Long userId, Long friendId) {
        try {
            UserFriendship uf = jdbcTemplate.queryForObject(SQL_GET_FRIENDSHIP, (rs, rowNum) ->
                            UserFriendship.builder()
                            .firstUserId(rs.getLong("FIRST_USER_ID"))
                            .secondUserId(rs.getLong("SECOND_USER_ID"))
                            .status(rs.getBoolean("STATUS")).build(), userId, friendId, userId, friendId);
            return Optional.ofNullable(uf);
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }
}