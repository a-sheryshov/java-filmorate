package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage extends ModelStorage<Event> {
    List<Event> readByUser(Long userId);
}
