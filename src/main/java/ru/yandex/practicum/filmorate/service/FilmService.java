package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@Validated
@Service
public class FilmService extends AbstractModelService<Film, FilmStorage> {
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage storage, UserService userService) {
        super(storage);
        this.userService = userService;
    }

    public List<Film> getPopular(Integer count) {
        return storage.getPopular(count);
    }

    public void addLike(Long id, Long userId) {
        userService.read(userId);
        Film film = storage.read(id);
        film.getLikes().add(userId);
        storage.saveLikes(film);
        log.info("Like from {} added to film {}", id, film);
    }

    public void deleteLike(Long id, Long userId) {
        Film film = storage.read(id);
        if (!film.getLikes().remove(userId)) {
            throw new ObjectNotFoundException("No such like available");
        }
        storage.saveLikes(film);
        log.info("Like from {} removed from film {}", id, film);
    }

    public void delete(Long filmId) {
        storage.delete(filmId);
        log.info("Film with id {} is deleted", filmId);
    }

}

