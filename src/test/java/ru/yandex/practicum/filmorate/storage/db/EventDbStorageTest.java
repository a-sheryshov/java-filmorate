package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventValue;
import ru.yandex.practicum.filmorate.model.OperationValue;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class EventDbStorageTest {
    private final EventDbStorage eventDbStorage;
    private final UserStorage userStorage;

    @Test
    void shouldCreateAndRead() {
        User user1 = getFirstTestUser();
        user1 = userStorage.create(user1);
        User user2 = getSecondTestUser();
        user2 = userStorage.create(user2);

        List<Event> event1 = eventDbStorage.readByUser(user1.getId());
        assertTrue(event1.isEmpty());
        List<Event> event2 = eventDbStorage.readByUser(user2.getId());
        assertTrue(event2.isEmpty());

        Event expectedEvent1 = eventDbStorage.create(new Event(
                new Date().getTime(), user1.getId(), EventValue.FRIEND, OperationValue.ADD, user2.getId()));

        Event expectedEvent2 = eventDbStorage.create(new Event(
                new Date().getTime(), user1.getId(), EventValue.FRIEND, OperationValue.REMOVE, user2.getId()));

        event1 = eventDbStorage.readByUser(user1.getId());
        assertEquals(2, event1.size());
        event2 = eventDbStorage.readByUser(user2.getId());
        assertTrue(event2.isEmpty());

        List<Event> allEvents = eventDbStorage.readAll();
        assertEquals(2, allEvents.size());

        Event readEvent = eventDbStorage.read(2L);
        assertEquals(2, readEvent.getId());

        List<Event> eventsForUser1 = eventDbStorage.readByUser(user1.getId());
        assertEquals(expectedEvent1, eventsForUser1.get(0));
        assertEquals(expectedEvent2, eventsForUser1.get(1));
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

}
