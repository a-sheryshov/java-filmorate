package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class RatingDbStorageTest {
    private final RatingDbStorage ratingStorage;
    private int recordCount;   //в таблице есть записи

    @BeforeEach
    void beforeEach() {
        recordCount = ratingStorage.readAll().size();
    }

    @Test
    void shouldReadCreatedRating() {
        long firstId = recordCount++;
        Rating expected = getFirstTestRating(firstId);
        ratingStorage.create(expected);
        Rating actual = ratingStorage.read(expected.getId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void shouldReadAllCreatedRatings() {
        long firstId = recordCount++;
        Rating firstExpected = getFirstTestRating(firstId);
        ratingStorage.create(firstExpected);
        long secondId = recordCount++;
        Rating secondExpected = getExpRating2(secondId);
        ratingStorage.create(secondExpected);

        List<Rating> actual = ratingStorage.readAll();
        int i1 = actual.size() - 2;
        int i2 = actual.size() - 1;
        assertEquals(firstExpected,  actual.get(i1));
        assertEquals(secondExpected, actual.get(i2));
        assertEquals(recordCount, actual.size());
    }

    @Test
    void shouldCreateRating() {
        long firstId = recordCount++;
        Rating expected = getFirstTestRating(firstId);
        ratingStorage.create(expected);
        Rating actual = ratingStorage.read(expected.getId());
        assertEquals(expected.getId(),actual.getId());
        assertEquals(expected.getName(),actual.getName());
    }

    @Test
    void shouldUpdateRating() {
        long firstId = recordCount++;
        Rating expected = getFirstTestRating(firstId);
        ratingStorage.create(expected);
        expected.setName("Expected");

        ratingStorage.update(expected);
        Rating actual = ratingStorage.read(expected.getId());

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    private Rating getFirstTestRating(Long id) {
        Rating gating = new Rating();
        gating.setId(id);
        gating.setName("Rating1");
        return gating;
    }

    private Rating getExpRating2(Long id) {
        Rating gating = new Rating();
        gating.setId(id);
        gating.setName("Rating2");
        return gating;
    }
}