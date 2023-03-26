package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;
    private int recordCount; //в таблице есть записи

    @BeforeEach
    void beforeEach() {
        recordCount = genreStorage.readAll().size();
    }

    @Test
    void shouldReadCreatedGenre() {
        long firstId = recordCount++;
        Genre expected = getFirstTestGenre(firstId);
        genreStorage.create(expected);
        Genre actual = genreStorage.read(expected.getId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void shouldReadAllCreatedGenres() {
        long id1 = recordCount++;
        Genre firstExpected = getFirstTestGenre(id1);
        genreStorage.create(firstExpected);
        long id2 = recordCount++;
        Genre secondExpected = getSecondTestGenre(id2);
        genreStorage.create(secondExpected);

        List<Genre> actual = genreStorage.readAll();
        int firstId = actual.size() - 2;
        int second_id = actual.size() - 1;
        assertEquals(firstExpected,  actual.get(firstId));
        assertEquals(secondExpected, actual.get(second_id));
        assertEquals(recordCount, actual.size());
    }

    @Test
    void shouldCreateGenre() {
        long firstId = recordCount++;
        Genre expected = getFirstTestGenre(firstId);
        genreStorage.create(expected);
        Genre actual = genreStorage.read(expected.getId());
        assertEquals(expected.getId(),actual.getId());
        assertEquals(expected.getName(),actual.getName());
    }

    @Test
    void shouldUpdateGenre() {
        long firstId = recordCount++;
        Genre expected = getFirstTestGenre(firstId);
        genreStorage.create(expected);
        expected.setName("action");

        genreStorage.update(expected);
        Genre actual = genreStorage.read(expected.getId());

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    private Genre getFirstTestGenre(Long id) {
        Genre genre = new Genre();
        genre.setId(id);
        genre.setName("FirstTest");
        return genre;
    }

    private Genre getSecondTestGenre(Long id) {
        Genre genre = new Genre();
        genre.setId(id);
        genre.setName("SecondTest");
        return genre;
    }
}