package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class EventDbStorageTest {
    private final EventDbStorage eventDbStorage;
    private final UserStorage userStorage;
//    private final FilmDbStorage filmStorage;

    @Test
    void create() {
        User user1 = getFirstTestUser();
        user1 = userStorage.create(user1);
        User user2 = getSecondTestUser();
        user2 = userStorage.create(user2);



        List<Event> event1 = eventDbStorage.read(Set.of(user1.getId()));
        assertTrue(event1.isEmpty());
        List<Event> event2 = eventDbStorage.read(Set.of(user2.getId()));
        assertTrue(event2.isEmpty());

        eventDbStorage.create(new Event(new Date().getTime(), user1.getId(), EventValue.FRIEND, OperationValue.ADD, user2.getId()));
        eventDbStorage.create(new Event(new Date().getTime(), user1.getId(), EventValue.REVIEW, OperationValue.UPDATE, 2));
//
        List<Event> events = eventDbStorage.readAll();
        assertEquals(2, events.size());
        System.out.println(events);

        Event e1 = eventDbStorage.read(2L);
        assertEquals(2, e1.getId());
        System.out.println(e1);

        List <Event>  e2 = eventDbStorage.read(Set.of(1L));
        System.out.println(e2);
        assertEquals(2, e1.getEntityId());
//        userStorage.createEvent();
//        userStorage.getEvent();
//        assertEquals(firstExpectedUser, firstActual);
//        assertEquals(firstExpectedUser, firstActual);
    }

    private User getFirstTestUser() {
        User user = new User();
        user.setEmail("test1@test.test");
        user.setLogin("test1");
        user.setName("User First");
        user.setBirthday(LocalDate.of(1999, 10, 9));
        return user;
    }

    private User getSecondTestUser() {
        User user = new User();
        user.setEmail("test2@test.test");
        user.setLogin("test2");
        user.setName("User Second");
        user.setBirthday(LocalDate.of(1988, 8, 8));
        return user;
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