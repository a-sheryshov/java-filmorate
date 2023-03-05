package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.AbstractModel;
import ru.yandex.practicum.filmorate.service.ModelService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Slf4j
public abstract class AbstractModelController<T extends AbstractModel, S extends ModelService<T>> {
    static final String INFO_LOG_MSG_RGX = "Request '{}' to '{}', objectId: {}";
    final S service;

    @Autowired
    public AbstractModelController(S service) {
        this.service = service;
    }

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

    @DeleteMapping("/{id}")
    public void delete(@PathVariable final Long id, HttpServletRequest request) {
        log.info(INFO_LOG_MSG_RGX,
                request.getMethod(), request.getRequestURI(), id);
        service.delete(id);
    }

    @GetMapping("/{id}")
    public T read(@PathVariable final Long id, HttpServletRequest request) {
        log.info(INFO_LOG_MSG_RGX,
                request.getMethod(), request.getRequestURI(), id);
        return service.read(id);
    }

    @GetMapping
    public Collection<T> readAll() {
        return service.readAll();
    }

}
