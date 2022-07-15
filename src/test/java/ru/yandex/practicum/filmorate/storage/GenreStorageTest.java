package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GenreStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Test
    void checkGetGenreById() {
        Genre genreOne = genreDbStorage.getGenreById(1);
        assertEquals("Комедия", genreOne.getName());
    }

    @Test
    void checkGetAllGenres() {
        Collection<Genre> actualResult = genreDbStorage.getAllGenres();
        assertEquals(6, actualResult.size());
    }
}