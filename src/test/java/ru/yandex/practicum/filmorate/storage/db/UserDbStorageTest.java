package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserDbStorageTest {
    private final UserDbStorage userStorage;
    private static final String EMAIL1 = "test1@test.test";
    private static final String EMAIL2 = "test2@test.test";

    @Test
    void shouldReadCreatedUser() {
        User expected = getFirstTestUser();
        userStorage.create(expected);
        User actual = userStorage.read(expected.getId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void shouldReadAllCreatedUsers() {
        User firstExpectedUser = getFirstTestUser();
        userStorage.create(firstExpectedUser);
        User secondExpectedUser = getSecondTestUser();
        userStorage.create(secondExpectedUser);
        List<User> expected = List.of(firstExpectedUser, secondExpectedUser);

        List<User> actual = userStorage.readAll();
        assertEquals(expected, actual);
        assertEquals(2, actual.size());
    }

    @Test
    void shouldCreateUser() {
        User expected = getFirstTestUser();
        userStorage.create(expected);
        User actual = userStorage.read(expected.getId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getLogin(), actual.getLogin());
        assertEquals(expected.getBirthday(), actual.getBirthday());
    }

    @Test
    void shouldUpdateUser() {
        User expected = getFirstTestUser();
        userStorage.create(expected);
        expected.setName("no user");

        userStorage.update(expected);
        User actual = userStorage.read(expected.getId());

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    void shouldAddFriend() {
        User firstExpectedUser = getFirstTestUser();
        firstExpectedUser = userStorage.create(firstExpectedUser);
        User secondExpectedUser = getSecondTestUser();
        secondExpectedUser = userStorage.create(secondExpectedUser);
        Set<Long> actualFriends = firstExpectedUser.getFriends();
        assertTrue(actualFriends.isEmpty());
        Set<Long> actualFriends2 = secondExpectedUser.getFriends();
        assertTrue(actualFriends2.isEmpty());
        firstExpectedUser.getFriends().add(secondExpectedUser.getId());
        secondExpectedUser.getFriends().add(firstExpectedUser.getId());
        userStorage.insertFriendship(firstExpectedUser.getId(), secondExpectedUser.getId());
        userStorage.updateFriendship(firstExpectedUser.getId(), secondExpectedUser.getId()
                , true, firstExpectedUser.getId(), secondExpectedUser.getId());
        User firstActual = userStorage.read(firstExpectedUser.getId());
        User secondActual = userStorage.read(secondExpectedUser.getId());
        assertEquals(firstExpectedUser, firstActual);
        assertEquals(secondExpectedUser, secondActual);
        assertTrue(userStorage.containsFriendship(firstExpectedUser.getId()
                , secondExpectedUser.getId(), true));


    }

    @Test
    void shouldRemoveFriend() {
        User firstExpectedUser = getFirstTestUser();
        firstExpectedUser = userStorage.create(firstExpectedUser);
        User secondExpectedUser = getSecondTestUser();
        secondExpectedUser = userStorage.create(secondExpectedUser);
        Set<Long> actualFriends = firstExpectedUser.getFriends();
        assertTrue(actualFriends.isEmpty());
        Set<Long> actualFriends2 = secondExpectedUser.getFriends();
        assertTrue(actualFriends2.isEmpty());
        firstExpectedUser.getFriends().add(secondExpectedUser.getId());
        secondExpectedUser.getFriends().add(firstExpectedUser.getId());
        userStorage.insertFriendship(firstExpectedUser.getId(), secondExpectedUser.getId());
        userStorage.updateFriendship(firstExpectedUser.getId(), secondExpectedUser.getId()
                , true, firstExpectedUser.getId(), secondExpectedUser.getId());
        User firstActual = userStorage.read(firstExpectedUser.getId());
        User secondActual = userStorage.read(secondExpectedUser.getId());
        assertEquals(firstExpectedUser, firstActual);
        assertEquals(secondExpectedUser, secondActual);
        assertTrue(userStorage.containsFriendship(firstExpectedUser.getId()
                , secondExpectedUser.getId(), true));
        userStorage.removeFriendship(firstExpectedUser.getId(), secondExpectedUser.getId());
        userStorage.removeFriendship(secondExpectedUser.getId(), firstExpectedUser.getId());
        firstExpectedUser.getFriends().remove(secondExpectedUser.getId());
        secondExpectedUser.getFriends().remove(firstExpectedUser.getId());
        firstActual = userStorage.read(firstExpectedUser.getId());
        secondActual = userStorage.read(secondExpectedUser.getId());
        assertEquals(firstExpectedUser, firstActual);
        assertEquals(secondExpectedUser, secondActual);
        assertFalse(userStorage.containsFriendship(firstExpectedUser.getId()
                , secondExpectedUser.getId(), true));

    }

    private User getFirstTestUser() {
        User user = new User();
        user.setEmail(EMAIL1);
        user.setLogin("test1");
        user.setName("User First");
        user.setBirthday(LocalDate.of(1999, 10, 9));
        return user;
    }

    private User getSecondTestUser() {
        User user = new User();
        user.setEmail(EMAIL2);
        user.setLogin("test2");
        user.setName("User Second");
        user.setBirthday(LocalDate.of(1988, 8, 8));
        return user;
    }
}

