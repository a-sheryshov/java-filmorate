package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DirectorStorage extends ModelStorage<Director> {
    Set<Director> readDirectorsByFilm(Long filmId);

    Map<Long, Set<Director>> readDirectorsByFilm(List<Long> filmIds);

    void checkDirector(Long id);

    void delete(Long directorId);


}