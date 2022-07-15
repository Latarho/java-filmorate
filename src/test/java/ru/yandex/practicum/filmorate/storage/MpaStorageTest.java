package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MpaStorageTest {

    private final MpaDbStorage mpaDbStorage;

    @Test
    void checkGetMpaRatingById() {
        MpaRating mpaRatingOne = mpaDbStorage.getMpaRatingById(1);
        assertEquals("G", mpaRatingOne.getName());
    }

    @Test
    void checkGetAllMpa() {
        Collection<MpaRating> actualResult = mpaDbStorage.getAllMpa();
        assertEquals(5, actualResult.size());
    }
}