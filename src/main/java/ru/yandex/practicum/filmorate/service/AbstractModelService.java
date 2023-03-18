package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.AbstractModel;
import ru.yandex.practicum.filmorate.storage.ModelStorage;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@Validated
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractModelService<T extends AbstractModel> implements ModelService<T> {
    final ModelStorage<T> storage;
    private long counter = 0L;

    public T create(@Valid final T data) {
        data.setId(++counter);
        storage.create(data);
        log.info("{} added: {}", data.getClass().getSimpleName(), data.getId());
        return data;
    }

    public T update(@Valid final T data) throws ObjectNotFoundException {
        storage.update(data);
        log.info("{} updated: {}", data.getClass().getSimpleName(), data.getId());
        return data;
    }

    public T read(@Valid @Positive final Long id) throws ObjectNotFoundException {
        return storage.read(id);
    }

    public Collection<T> readAll() {
        return storage.readAll();
    }

    public void delete(@Valid @Positive final Long id) throws ObjectNotFoundException {
        storage.delete(id);
        log.info("{} deleted from {}", id, storage.getClass().getSimpleName());
    }
}
