package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface RecommendationStorage {
    Long getUserIdWithMaxLikes(Long userId);

    List<Film> getLikedFilms(Long userId, Long userToRecommendationId);
}
