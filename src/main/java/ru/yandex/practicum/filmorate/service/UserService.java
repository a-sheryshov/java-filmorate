package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.*;

@Service
@Validated
@Slf4j
public class UserService extends AbstractModelService<User, UserStorage> {
    @Autowired
    public UserService(final UserStorage storage) {
        super(storage);
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

    public Collection<User> getFriends(@Valid @Positive Long id) throws ObjectNotFoundException {
        List<User> friends = new ArrayList<>();
        for (Long friendId : storage.read(id).getFriends()) {
            friends.add(storage.read(friendId));

        }
        return friends;
    }

    public Collection<User> getCommonFriends(@Valid @Positive Long firstUserid
            , @Valid @Positive Long secondUserId) throws ObjectNotFoundException {
        List<User> commonFriends = new ArrayList<>();
        Set<Long> secondUsersFriends = storage.read(secondUserId).getFriends();
        Set<Long> firstUsersFriends = storage.read(firstUserid).getFriends();
        for (Long friendId : firstUsersFriends) {
            if (secondUsersFriends.contains(friendId)) {
                commonFriends.add(storage.read(friendId));
            }
        }
        return commonFriends;
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
        log.info("Friendship between {} and {} removed", friendId, user);
    }
}
