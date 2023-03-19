package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.AbstractModel;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class InMemoryAbstractModelStorage<T extends AbstractModel> implements ModelStorage<T> {
    private final Map<Long, T>  storage = new HashMap<>();

    public T create(final @NotNull T data) {
        storage.put(data.getId(), data);
        return data;
    }

    public T update(final @NotNull T data) throws ObjectNotFoundException {
        long id = data.getId();
        if (storage.containsKey(id)) {
            storage.put(id, data);
            return data;
        } else {
            throw new ObjectNotFoundException("Object not found: id " + id);
        }
    }

    public void delete(final @NotNull Long id) throws ObjectNotFoundException {
        if (storage.containsKey(id)) {
            storage.remove(id);
        } else {
            throw new ObjectNotFoundException("Object not found: id " + id);
        }
    }

    public T read(final @NotNull Long id) throws ObjectNotFoundException {
        if (storage.containsKey(id)) {
            return storage.get(id);
        } else {
            throw new ObjectNotFoundException("Object not found: id " + id);
        }
    }

    public List<T> readAll() {
        return new ArrayList<>(storage.values());
    }

}
