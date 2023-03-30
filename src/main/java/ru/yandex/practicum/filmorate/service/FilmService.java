package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventValue;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.OperationValue;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class FilmService extends AbstractModelService<Film, FilmStorage> {
    private final UserService userService;
    private final EventStorage eventStorage;

    @Autowired
    public FilmService(FilmStorage storage, UserService userService, EventStorage eventStorage) {
        super(storage);
        this.userService = userService;
        this.eventStorage = eventStorage;
    }

    public List<Film> getPopular(Integer count) {
        return storage.getPopular(count);
    }

    public void addLike(Long id, Long userId) {
        userService.read(userId);
        Film film = storage.read(id);
        film.getLikes().add(userId);
        storage.saveLikes(film);
        eventStorage.create(new Event(new Date().getTime(), userId, EventValue.LIKE, OperationValue.ADD, id));
        log.info("Like from {} added to film {}", id, film);
    }

    public void deleteLike(Long id, Long userId) {
        Film film = storage.read(id);
        if (!film.getLikes().remove(userId)) {
            throw new ObjectNotFoundException("No such like available");
        }
        storage.saveLikes(film);
        eventStorage.create(new Event(new Date().getTime(), userId, EventValue.LIKE, OperationValue.REMOVE, id));
        log.info("Like from {} removed from film {}", id, film);
    }

}
