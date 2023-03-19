package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.AbstractModel;

import java.util.List;


public interface ModelStorage<T extends AbstractModel> {
    T create(T t);

    T update(T t);

    void delete(Long id);

    T read(Long id);

    List<T> readAll();
}
