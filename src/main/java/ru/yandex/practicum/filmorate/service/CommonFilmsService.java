package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.CommonFilmsStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CommonFilmsService {

    private UserDbStorage userDbStorage;
    private CommonFilmsStorage commonFilmsStorage;

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        userDbStorage.checkUser(userId);
        userDbStorage.checkUser(friendId);
        return commonFilmsStorage.getCommonFilms(userId, friendId);
    }
}
