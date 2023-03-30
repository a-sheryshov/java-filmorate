package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventValue;
import ru.yandex.practicum.filmorate.model.OperationValue;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
@Slf4j
public class UserService extends AbstractModelService<User, UserStorage> {
    private final EventStorage eventStorage;

    @Autowired
    public UserService(final UserStorage storage, EventStorage eventStorage) {
        super(storage);
        this.eventStorage = eventStorage;
    }

    @Override
    public User create(final User user) {
        checkName(user);
        return super.create(user);
    }

    @Override
    public User update(final User user) {
        checkName(user);
        return super.update(user);
    }

    private void checkName(final User user) {
        Optional<String> optionalName = Optional.ofNullable(user.getName());
        optionalName.ifPresentOrElse((name) -> {
            if (name.isBlank()) {
                user.setName(user.getLogin());
            }
        }, () -> user.setName(user.getLogin()));
    }

    public List<User> getFriends(@Valid @Positive Long id) {
        Set<Long> friendsIds = new HashSet<>(storage.read(id).getFriends());
        return storage.read(friendsIds);
    }

    public List<User> getCommonFriends(@Valid @Positive Long firstUserid
            , @Valid @Positive Long secondUserId) {
        Set<Long> firstUsersFriends = storage.read(firstUserid).getFriends();
        Set<Long> secondUsersFriends = storage.read(secondUserId).getFriends();
        Set<Long> commonFriends = firstUsersFriends.stream()
                .filter(two -> secondUsersFriends.stream()
                        .anyMatch(one -> one.equals(two)))
                .collect(Collectors.toSet());
        if (commonFriends.size() != 0) {
            return storage.read(commonFriends);
        }
        return new ArrayList<>();

    }

    public void addFriend(Long id, Long friendId) {
        User user = storage.read(id);
        storage.read(friendId);
        if (user.getFriends().contains(friendId)) {
            log.info("Friendship between {} and {} exists", id, friendId);
            return;
        }
        user.getFriends().add(friendId);
        if (storage.containsFriendship(friendId, id, false)) {
            //friendId добавил ранее в друзья
            storage.updateFriendship(friendId, id, true, friendId, id);
        } else if (!storage.containsFriendship(id, friendId, null)) {
            //Односторонняя связь
            storage.insertFriendship(id, friendId);
        }
        eventStorage.create(new Event(new Date().getTime(), id, EventValue.FRIEND, OperationValue.ADD, friendId));
        log.info("Friendship between {} and {} added", id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        User user = storage.read(id);
        storage.read(friendId);
        if (!user.getFriends().contains(friendId)) {
            log.warn("Friendship between {} and {} not exists", id, friendId);
            return;
        }
        user.getFriends().remove(friendId);

        if (storage.containsFriendship(id, friendId, false)) {
            //Односторонняя связь
            storage.removeFriendship(id, friendId);
        } else if (storage.containsFriendship(id, friendId, true)) {
            //двойная связь
            storage.updateFriendship(friendId, id, false, id, friendId);
        } else if (storage.containsFriendship(friendId, id, true)) {
            //двойная связь. friendId  добавил первым
            storage.updateFriendship(friendId, id, false, friendId, id);
        }
        eventStorage.create(new Event(new Date().getTime(), id, EventValue.FRIEND, OperationValue.REMOVE, friendId));
        log.info("Friendship between {} and {} removed", friendId, user);
    }

    public List<Event> getEvent(Long id) {
        storage.read(id);
        return eventStorage.read(Set.of(id));
    }

}
