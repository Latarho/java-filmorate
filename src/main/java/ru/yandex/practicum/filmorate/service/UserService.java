package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.userFriendship.UserFriendshipStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//TODO Доделать!
// Аннотация указывает, что класс нужно добавить в контекст
@Service
public class UserService {

    private final UserStorage userStorage;
    private final UserFriendshipStorage userFriendshipStorage;
    private Long id = 1L;

    // Сообщаем Spring, что нужно передать в конструктор объект класса UserStorage
    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("userFriendshipDbStorage") UserFriendshipStorage userFriendshipStorage) {
        this.userStorage = userStorage;
        this.userFriendshipStorage = userFriendshipStorage;
    }

    public User createUser(User user) {
        String userName = validationUser(user);

        for (User u : userStorage.getAllUsers()) {
            if (u.getEmail().equals(user.getEmail()) || u.getLogin().equals(user.getLogin())) {
                throw new ValidationException("Эмейл или логин уже уже используются.");
            }
        }

        LocalDate userBirthday = user.getBirthday();
        User userBuild = User.builder()
                .id(generateId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(userName)
                .birthday(userBirthday)
                .build();
        userStorage.createUser(userBuild);
        return userBuild;
    }

    public User getUserById(Long id) {
        Optional<User> user = userStorage.getUserById(id);
        return user.orElseThrow(() -> new DataNotFoundException("Пользователь c Id: " + id + "не найден."));
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User updateUser(User user) {
        if (user.getId() != null) {
            Optional<User> userFromStorage = userStorage.getUserById(user.getId());
            userFromStorage.orElseThrow(() -> new DataNotFoundException("Пользователь: " + user + "не найден."));
                String name = validationUser(user);
                if (!userFromStorage.get().getEmail().equals(user.getEmail()) ||
                        !userFromStorage.get().getLogin().equals(user.getLogin())) {
                    for (User u : userStorage.getAllUsers()) {
                        if (u.getEmail().equals(user.getEmail()) || u.getLogin().equals(user.getLogin())) {
                            if (!u.getId().equals(userFromStorage.get().getId()))
                                throw new ValidationException("Эмейл или логин уже уже используются.");
                        }
                    }
                }

                LocalDate userBirthday = user.getBirthday();
                User userUpdate = User.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .login(user.getLogin())
                        .name(name)
                        .birthday(userBirthday)
                        .build();
                return userStorage.updateUser(userUpdate);
        } else {
            throw new ValidationException("Указан некорректный Id.");
        }
    }

    public void deleteById(Long id) {
        if (id <= 0) {
            throw new ValidationException("Передан некорректный Id пользователя.");
        }
        userStorage.deleteById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new DataNotFoundException("Передан некорректный Id пользователя.");
        }
        userFriendshipStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new ValidationException("Передан некорректный Id пользователя.");
        }

        userFriendshipStorage.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriends (Long userId) {
        if (userId <=0) {
            throw new ValidationException("Передан некорректный Id пользователя.");
        }
        List<Long> friends = userFriendshipStorage.getUserFriends(userId);
        if (friends.isEmpty()) {
            return List.of();
        } else {
            List<User> userFriends = new ArrayList<>();
            for (Long id : friends) {
                userFriends.add(getUserById(id));
            }
            return userFriends;
        }
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        if (userId <= 0 || otherId <= 0) {
            throw new ValidationException("Передан некорректный Id пользователя.");
        }

        List<Long> userFromStorage = userFriendshipStorage.getUserFriends(userId);
        List<Long> otherUserFromStorage = userFriendshipStorage.getUserFriends(otherId);

        List<Long> friends = userFromStorage.stream()
                .distinct().filter(otherUserFromStorage::contains).collect(Collectors.toList());

        if (friends.isEmpty()) {
            return List.of();
        } else {
            List<User> commonFriends = new ArrayList<>();
            for (Long id : friends) {
                commonFriends.add(getUserById(id));
            }
            return commonFriends;
        }
    }

    /**
     * Валидация экземпляра класса User.
     *
     * @param user объект класса User (из тела запроса).
     * @return Name пользователя.
     */
    public String validationUser(User user) {
        LocalDate userBirthday = user.getBirthday();
        if (userBirthday.isAfter(LocalDate.now()))
            throw new ValidationException("Дата рождения пользователя должна быть раньше текущей даты.");

        String name;
        if (user.getName().isEmpty()) {
            name = user.getLogin();
        } else {
            name = user.getName();
        }
        return name;
    }

    /**
     * Генерация уникального Id пользователя.
     *
     * @return Id пользователя.
     */
    private Long generateId() {
        return id++;
    }
}