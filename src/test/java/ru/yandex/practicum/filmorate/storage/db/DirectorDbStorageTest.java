package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DirectorDbStorageTest {
    private final DirectorDbStorage directorStorage;

    @BeforeEach
    void shouldCreateDirector() {
        Director expected = getFirstTestDirector();
        directorStorage.create(expected);
        Director actual = directorStorage.read(expected.getId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void shouldReadCreatedDirector() {
        Director expected = getFirstTestDirector();
        Director actual = directorStorage.read(expected.getId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void shouldReadAllCreatedDirectors() {
        Director firstExpected = getFirstTestDirector();
        Director secondExpected = getSecondTestDirector();
        directorStorage.create(secondExpected);

        List<Director> actual = directorStorage.readAll();
        int firstId = actual.size() - 2;
        int secondId = actual.size() - 1;
        assertEquals(firstExpected, actual.get(firstId));
        assertEquals(secondExpected, actual.get(secondId));
        assertEquals(2, actual.size());
    }


    @Test
    void shouldUpdateDirector() {
        Director expected = getFirstTestDirector();
        expected.setName("action");

        directorStorage.update(expected);
        Director actual = directorStorage.read(expected.getId());

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    private Director getFirstTestDirector() {
        Director director = new Director();
        director.setId(1L);
        director.setName("Vachovski brothers");
        return director;
    }

    private Director getSecondTestDirector() {
        Director director = new Director();
        director.setId(2L);
        director.setName("Vachovski sisters");
        return director;
    }
}