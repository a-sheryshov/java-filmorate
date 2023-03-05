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
public class UserService extends AbstractModelService<User> {
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

    @Override
    public void delete(@Valid @Positive final Long id) throws ObjectNotFoundException {
        for (Long friendId : storage.read(id).getFriends()) {
            User friend = storage.read(friendId);
            friend.getFriends().remove(id);
            storage.update(friend);
        }
        super.delete(id);
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
        for (Long friendId : storage.read(firstUserid).getFriends()) {
            if (secondUsersFriends.contains(friendId)) {
                commonFriends.add(storage.read(friendId));
            }
        }
        return commonFriends;
    }

    public void addFriend(final Long id, final Long friendId)
            throws ObjectNotFoundException {
        User user = storage.read(id);
        User friend = storage.read(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        storage.update(user);
        storage.update(friend);
        log.info("Friendship between {} and {} added", friendId, user);
    }

    public void deleteFriend(final Long id, final Long friendId)
            throws ObjectNotFoundException {
        User user = storage.read(id);
        User friend = storage.read(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        storage.update(user);
        storage.update(friend);
        log.info("Friendship between {} and {} removed", friendId, user);
    }
}
