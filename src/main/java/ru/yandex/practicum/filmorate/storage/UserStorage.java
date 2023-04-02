package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage extends ModelStorage<User> {

    boolean containsFriendship(Long filterId1, Long filterId2, Boolean filterConfirmed);

    void updateFriendship(Long id1, Long id2, boolean confirmed, Long filterId1, Long filterId2);

    void insertFriendship(Long id, Long friendId);

    void removeFriendship(Long filterId1, Long filterId2);

    void delete(Long userId);

    void checkUser(Long id);
}
