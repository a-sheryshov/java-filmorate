package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.AbstractModel;

import java.util.List;
import java.util.Set;


public interface ModelStorage<T extends AbstractModel> {
    T create(T t);

    T update(T t);

    T read(Long id);

    List<T> read(Set<Long> id_set);

    List<T> readAll();
}
