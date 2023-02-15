package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
public class FilmController extends AbstractController<Film> {

    @PostMapping
    public Film createFilmAction(@Valid @RequestBody Film film
            , HttpServletRequest request){
        save(film);
        log.info(INFO_LOG_MSG_RGX,
                request.getMethod(), request.getRequestURI(), film.getId());
        return film;
    }
    @PutMapping
    public Film updateFilmAction(@Valid @RequestBody Film film
            , HttpServletRequest request){
        update(film);
        log.info(INFO_LOG_MSG_RGX,
                request.getMethod(), request.getRequestURI(), film.getId());
        return film;
    }
    @GetMapping
    public List<Film> getAllFilmsAction() {
        return getAll();
    }

}
