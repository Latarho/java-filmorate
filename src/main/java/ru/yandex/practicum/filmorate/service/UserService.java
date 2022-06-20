package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserDto;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Аннотация указывает, что класс нужно добавить в контекст
@Service
public class UserService {
    private final UserStorage userStorage;
    private Long id = 1L;

    // Сообщаем Spring, что нужно передать в конструктор объект класса UserStorage
    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(UserDto userDto) {
        String userName = validationUser(userDto);

        for (User user : userStorage.getAllUsers()) {
            if (user.getEmail().equals(userDto.getEmail()) || user.getLogin().equals(userDto.getLogin())) {
                throw new ValidationException("Эмейл или логин уже уже используются.");
            }
        }

        LocalDate userBirthday = LocalDate.parse(userDto.getBirthday());
        User userBuild = User.builder()
                .id(generateId())
                .email(userDto.getEmail())
                .login(userDto.getLogin())
                .name(userName)
                .birthday(userBirthday)
                .build();
        userStorage.createUser(userBuild);
        return userBuild;
    }

    public User getUserById(Long id) {
        Optional<User> user = userStorage.getUserById(id);
        if (user.isPresent())
            return user.get();
        throw new DataNotFoundException("Пользователь c Id: " + id + "не найден.");
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User updateUser(UserDto userDto) {
        User userBuild;

        if (userDto.getId() != null) {
            Optional<User> userFromStorage = userStorage.getUserById(userDto.getId());
            if (userFromStorage.isPresent()) {
                String name = validationUser(userDto);
                if (!userFromStorage.get().getEmail().equals(userDto.getEmail()) ||
                        !userFromStorage.get().getLogin().equals(userDto.getLogin())) {
                    for (User user : userStorage.getAllUsers()) {
                        if (user.getEmail().equals(userDto.getEmail()) ||
                                user.getLogin().equals(userDto.getLogin())) {
                            if (!user.getId().equals(userFromStorage.get().getId()))
                                throw new ValidationException("Эмейл или логин уже уже используются.");
                        }
                    }
                }

                LocalDate userBirthday = LocalDate.parse(userDto.getBirthday());
                userBuild = User.builder()
                        .id(userDto.getId())
                        .email(userDto.getEmail())
                        .login(userDto.getLogin())
                        .name(name)
                        .birthday(userBirthday)
                        .build();
                User userToAddFriends = userFromStorage.get();
                userBuild.setFriends(userToAddFriends.getFriends());
                userStorage.createUser(userBuild);
            } else
                throw new DataNotFoundException("Пользователь: " + userDto + "не найден.");
        } else {
            throw new ValidationException("Указан некорректный Id.");
        }
        return userBuild;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId <= 0 || friendId <= 0) {
            // В тестах постман здесь ожидается статус код 404, однако по факту эта операция соответсвует 400 коду, следовательно здесь должен быть ValidationException.
            // Переделать тесты в постмане.
            throw new DataNotFoundException("Передан некорректный Id пользователя.");
        }

        Optional<User> userFromStorage = userStorage.getUserById(userId);
        Optional<User> friendFromStorage = userStorage.getUserById(friendId);
        if (userFromStorage.isPresent() && friendFromStorage.isPresent()) {
            User user = userFromStorage.get();
            user.addFriend(friendId);
            userStorage.createUser(user);

            User friend = friendFromStorage.get();
            friend.addFriend(userId);
            userStorage.createUser(friend);
        } else
            throw new DataNotFoundException("Пользователь не найден.");
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new ValidationException("Передан некорректный Id пользователя.");
        }

        Optional<User> userFromStorage = userStorage.getUserById(userId);
        Optional<User> friendFromStorage = userStorage.getUserById(friendId);
        if (userFromStorage.isPresent() && friendFromStorage.isPresent()) {
            User user = userFromStorage.get();
            user.removeFriend(friendId);
            userStorage.createUser(user);

            User friend = friendFromStorage.get();
            friend.removeFriend(userId);
            userStorage.createUser(friend);
        } else
            throw new DataNotFoundException("Пользователь не найден.");
    }

    public List<User> getUserFriends (Long userId) {
        if (userId <=0) {
            throw new ValidationException("Передан некорректный Id пользователя.");
        }

        ArrayList<User> userFriends = new ArrayList<>();
        Optional<User> userFromStorage = userStorage.getUserById(userId);
        if (userFromStorage.isPresent()) {
            User user = userFromStorage.get();
            for (Long id : user.getFriends()) {
                    userFriends.add(userStorage.getUserById(id).get());
            }
            return userFriends;
        } else
            throw new DataNotFoundException("Пользователь не найден.");
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        if (userId <= 0 || otherId <= 0) {
            throw new ValidationException("Передан некорректный Id пользователя.");
        }

        Optional<User> userFromStorage = userStorage.getUserById(userId);
        Optional<User> maybeOtherUser = userStorage.getUserById(otherId);
        if (userFromStorage.isPresent() && maybeOtherUser.isPresent()) {
            List<User> userFriends = getUserFriends(userId);
            List<User> otherUserFriends = getUserFriends(otherId);
            userFriends.retainAll(otherUserFriends);
            return userFriends;
        }
        throw new DataNotFoundException("Пользователь не найден.");
    }

    /**
     * Валидация экземпляра класса User.
     *
     * @param userDto объект класса User (из тела запроса).
     * @return Name пользователя.
     */
    public String validationUser(UserDto userDto) {
        LocalDate userBirthday = LocalDate.parse(userDto.getBirthday());
        if (userBirthday.isAfter(LocalDate.now()))
            throw new ValidationException("Дата рождения пользователя должна быть раньше текущей даты.");

        String name;
        if (userDto.getName().isEmpty()) {
            name = userDto.getLogin();
        } else {
            name = userDto.getName();
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