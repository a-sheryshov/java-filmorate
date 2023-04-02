package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

@RestController
@RequestMapping("/directors")
public class DirectorController extends AbstractModelController<Director, DirectorService> {
    @Autowired
    public DirectorController(DirectorService service) {
        super(service);
    }

    @DeleteMapping("/{directorId}")
    public void delete(@PathVariable Long directorId) {
        service.delete(directorId);
    }
}