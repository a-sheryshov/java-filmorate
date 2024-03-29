package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.AbstractModel;
import ru.yandex.practicum.filmorate.service.ModelService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractModelController<T extends AbstractModel, S extends ModelService<T>> {
    private static final String INFO_LOG_MSG_RGX = "Request '{}' to '{}', objectId: {}";
    final S service;

    @PostMapping
    public T create(@RequestBody final T data, HttpServletRequest request) {
        log.info(INFO_LOG_MSG_RGX,
                request.getMethod(), request.getRequestURI(), data.getId());
        return service.create(data);
    }

    @PutMapping
    public T update(@RequestBody final T data, HttpServletRequest request) {
        log.info(INFO_LOG_MSG_RGX,
                request.getMethod(), request.getRequestURI(), data.getId());
        return service.update(data);
    }

    @GetMapping("/{id}")
    public T read(@PathVariable final Long id, HttpServletRequest request) {
        log.info(INFO_LOG_MSG_RGX,
                request.getMethod(), request.getRequestURI(), id);
        return service.read(id);
    }

    @GetMapping
    public List<T> readAll() {
        return service.readAll();
    }

}
