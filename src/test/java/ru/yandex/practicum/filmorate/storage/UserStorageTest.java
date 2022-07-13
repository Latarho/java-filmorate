package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserStorageTest {

    private final UserDbStorage userDbStorage;
    private User user;

    @BeforeEach
    public void BeforeEach() {
        user = User.builder()
                .id(1L)
                .email("latarho@gmail.com")
                .login("Latarho")
                .name("Serg")
                .birthday(LocalDate.of(1991, Month.OCTOBER, 9))
                .build();
    }

    @Test
    void checkCreateUser() throws DataNotFoundException {
        User userCreate = userDbStorage.createUser(user);
        int actualResult = userDbStorage.getAllUsers().size();
        assertEquals(actualResult, 1);
    }

    @Test
    void checkGetUserById() throws DataNotFoundException {
        User userCreate = userDbStorage.createUser(user);
        Optional<User> actualResult = userDbStorage.getUserById(1L);
        assertEquals(actualResult.get().getId(), userCreate.getId());
        assertEquals(actualResult.get().getEmail(), userCreate.getEmail());
        assertEquals(actualResult.get().getLogin(), userCreate.getLogin());
        assertEquals(actualResult.get().getName(), userCreate.getName());
        assertEquals(actualResult.get().getBirthday(), userCreate.getBirthday());
    }

    @Test
    void checkGetAllUsers() throws DataNotFoundException {
        User userCreate = userDbStorage.createUser(user);
        List<User> actualResult = userDbStorage.getAllUsers();
        assertEquals(actualResult.get(0).getId(), userCreate.getId());
    }

    @Test
    void checkUpdateUser() throws DataNotFoundException {
        User userCreate = userDbStorage.createUser(user);
        User userUp = User.builder()
                .id(1L)
                .email("latarho3@gmail.com")
                .login("Latarho3")
                .name("Serg3")
                .birthday(LocalDate.of(1990, Month.OCTOBER, 10))
                .build();
        User userUpdate = userDbStorage.updateUser(userUp);
        Optional<User> actualResult = userDbStorage.getUserById(1L);
        assertEquals(actualResult.get().getId(), userUpdate.getId());
        assertEquals(actualResult.get().getEmail(), userUpdate.getEmail());
        assertEquals(actualResult.get().getLogin(), userUpdate.getLogin());
        assertEquals(actualResult.get().getName(), userUpdate.getName());
        assertEquals(actualResult.get().getBirthday(), userUpdate.getBirthday());
    }

    @Test
    void checkDeleteById() throws DataNotFoundException {
        User userCreate = userDbStorage.createUser(user);
        int actualResultBeforeDeleting = userDbStorage.getAllUsers().size();
        assertEquals(actualResultBeforeDeleting, 1);

        userDbStorage.deleteById(userCreate.getId());
        int actualResultAfterDeleting = userDbStorage.getAllUsers().size();
        assertEquals(actualResultAfterDeleting, 0);
    }
}