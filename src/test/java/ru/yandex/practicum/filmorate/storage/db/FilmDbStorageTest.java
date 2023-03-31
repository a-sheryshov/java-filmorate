package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @Test
    void shouldReadCreatedFilm() {
        Film expected = getFirstTestFilm();
        filmStorage.create(expected);
        Film actFilm = filmStorage.read(expected.getId());
        assertEquals(expected.getId(), actFilm.getId());
        assertEquals(expected.getName(), actFilm.getName());
    }

    @Test
    void shouldReadAllCreatedFilms() {
        Film expected = getFirstTestFilm();
        filmStorage.create(expected);
        Film secondExpected = getSecondTestFilm();
        filmStorage.create(secondExpected);
        List<Film> actualFilms = filmStorage.readAll();
        assertEquals(2, actualFilms.size());
        assertEquals(expected.getName(), actualFilms.get(0).getName());
        assertEquals(expected.getId(), actualFilms.get(0).getId());
        assertEquals(secondExpected.getName(), actualFilms.get(1).getName());
        assertEquals(secondExpected.getId(), actualFilms.get(1).getId());
    }

    @Test
    void shouldCreateFilm() {
        Film expected = getFirstTestFilm();
        filmStorage.create(expected);
        Film actFilm = filmStorage.read(expected.getId());
        assertEquals(expected.getId(),actFilm.getId());
        assertEquals(expected.getName(),actFilm.getName());
        assertEquals(expected.getDescription(),actFilm.getDescription());
        assertEquals(expected.getReleaseDate(),actFilm.getReleaseDate());
        assertEquals(expected.getDuration(),actFilm.getDuration());
        assertEquals(expected.getMpa().getId(),actFilm.getMpa().getId());
        assertEquals(expected.getGenres().size(),actFilm.getGenres().size());
    }

    @Test
    void shouldUpdateFilm() {
        Film expFilm = getFirstTestFilm();
        filmStorage.create(expFilm);
        expFilm.setName("Super Film");

        filmStorage.update(expFilm);
        Film actFilm = filmStorage.read(expFilm.getId());

        assertEquals(expFilm.getId(), actFilm.getId());
        assertEquals(expFilm.getName(), actFilm.getName());
    }

    private Film getFirstTestFilm() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Film1");
        film.setDescription("DESCRIPTION1");
        film.setReleaseDate(LocalDate.of(2022, 2, 22));
        film.setDuration(100);

        Rating rating = new Rating();
        rating.setId(1L);
        film.setMpa(rating);

        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        film.setGenres(Set.of(genre1, genre2));
        return film;
    }


    private Film getSecondTestFilm() {
        Film film = new Film();
        film.setId(2L);
        film.setName("Film2");
        film.setDescription("DESCRIPTION");
        film.setReleaseDate(LocalDate.of(2011, 1, 11));
        film.setDuration(88);
        Rating rating = new Rating();
        rating.setId(2L);
        film.setMpa(rating);
        return film;
    }
}