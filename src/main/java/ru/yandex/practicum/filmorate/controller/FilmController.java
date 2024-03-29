package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController extends AbstractModelController<Film, FilmService> {

    @Autowired
    public FilmController(FilmService service) {
        super(service);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        service.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findPopularMovies(@RequestParam(defaultValue = "10") Integer count,
                                        @RequestParam(required = false) Long genreId,
                                        @RequestParam(required = false) Integer year) {
        return service.getPopular(count, genreId, year);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable Long filmId) {
        service.delete(filmId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> readFilmsByDirector(@PathVariable Long directorId, @RequestParam String sortBy) {
        return service.readByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam("query") String query, @RequestParam("by") String by) {
        return service.searchFilms(query, by);
    }
}
