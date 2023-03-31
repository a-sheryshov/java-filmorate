package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends ModelStorage<Film> {

    void saveLikes(Film film);

    List<Film> getPopular(Integer count);

    void delete(Long filmIid);
}
