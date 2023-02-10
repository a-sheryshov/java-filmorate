package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.AbstractEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public abstract class AbstractController <T extends AbstractEntity> {
    final String INFO_LOG_MSG_RGX = "Request '{}' to '{}', objectId: {}";
    private final Map<Long, T> storage = new HashMap<>();
    private long counter = 0L;

    public T save(final T data) {
        data.setId(++counter);
        storage.put(data.getId(), data);
        return data;
    }

    public T update(final T data) {
        long id = data.getId();
        if (storage.containsKey(id)){
            storage.put(id, data);
            return data;
        } else {
            throw new ObjectNotFoundException("Object not found: id "+ id);
        }
    }

    public List<T> getAll() {
        return new ArrayList<>(storage.values());
    }

}
