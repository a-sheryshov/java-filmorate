package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.AbstractModel;
import ru.yandex.practicum.filmorate.storage.ModelStorage;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@Slf4j
@AllArgsConstructor
public abstract class AbstractModelService<T extends AbstractModel, S extends ModelStorage<T>> implements ModelService<T> {
    final S storage;

    public T create(@Valid final T data) {
        T result = storage.create(data);
        log.info("{} added: {}", data.getClass().getSimpleName(), data.getId());
        return result;
    }

    public T update(@Valid final T data) {
        T result = storage.update(data);
        log.info("{} updated: {}", data.getClass().getSimpleName(), data.getId());
        return result;
    }

    public T read(@Valid @Positive final Long id) {
        return storage.read(id);
    }

    public List<T> readAll() {
        return storage.readAll();
    }
}
