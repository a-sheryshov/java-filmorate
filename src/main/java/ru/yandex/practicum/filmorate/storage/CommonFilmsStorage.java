package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface CommonFilmsStorage {
    List<Film> getCommonFilms(Long userId, Long friendId);
}
