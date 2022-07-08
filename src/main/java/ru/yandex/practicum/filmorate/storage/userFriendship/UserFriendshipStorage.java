package ru.yandex.practicum.filmorate.storage.userFriendship;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserFriendshipStorage {

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<Long> getUserFriends (Long userId);
}