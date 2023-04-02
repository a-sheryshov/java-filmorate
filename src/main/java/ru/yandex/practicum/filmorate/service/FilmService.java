package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;


@Service
@Slf4j
public class FilmService extends AbstractModelService<Film, FilmStorage> {
    private final UserService userService;
    private final GenreService genreService;

    @Autowired
    public FilmService(FilmStorage storage, UserService userService, GenreService genreService) {
        super(storage);
        this.userService = userService;
        this.genreService = genreService;
    }

    public List<Film> getPopular(Integer count, Long genreId, Integer year) {
//        List<Film> getPopular = storage.getPopular(count);
//        if (genreId != null) {
//            Genre genre = genreService.read(genreId);
//            getPopular = getPopular.stream()
//                    .filter(film -> film.getGenres().contains(genre))
//                    .collect(Collectors.toList());
//        }
//        if (year != null) {
//            getPopular = getPopular.stream()
//                    .filter(film -> film.getReleaseDate().getYear() == year)
//                    .collect(Collectors.toList());
//        }
//        return getPopular;
        return storage.getPopular(count, genreId, year);
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

}
