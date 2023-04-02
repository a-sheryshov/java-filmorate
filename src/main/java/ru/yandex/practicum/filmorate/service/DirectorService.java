package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

@Service
@Slf4j
public class DirectorService extends AbstractModelService<Director, DirectorStorage> {
    @Autowired
    public DirectorService(DirectorStorage storage) {
        super(storage);
    }

    public void delete(Long directorId) {
        storage.delete(directorId);
        log.info("Director with id {} is deleted", directorId);
    }
}