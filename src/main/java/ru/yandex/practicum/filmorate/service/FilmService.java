package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FilmService extends AbstractModelService<Film> {
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage storage, UserService userService) {
        super(storage);
        this.userService = userService;
    }

    public Collection<Film> getPopular(Integer count) {
        return readAll()
                .stream()
                .sorted((Film film1, Film film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void addLike(Long id, Long userId)
            throws ObjectNotFoundException {
        User user = userService.read(userId);
        Film film = storage.read(id);
        film.getLikes().add(userId);
        user.getLikes().add(id);
        storage.update(film);
        userService.update(user);
        log.info("Like from {} added to film {}", id, film);
    }

    public void deleteLike(Long id, Long userId)
            throws ObjectNotFoundException {
        Film film = storage.read(id);
        if (!film.getLikes().remove(userId)) {
            throw new ObjectNotFoundException("No such like available");
        }
        User user = userService.read(userId);
        user.getLikes().remove(id);
        storage.update(film);
        userService.update(user);
        log.info("Like from {} removed from film {}", id, film);
    }

    @Override
    public void delete(final Long id) throws ObjectNotFoundException {
        Film film = storage.read(id);
        for (Long like : film.getLikes()) {
            User user = userService.read(like);
            user.getLikes().remove(id);
            userService.update(user);
        }
        storage.delete(id);
        log.info("{} deleted from {}", id, storage.getClass().getSimpleName());
    }


}
