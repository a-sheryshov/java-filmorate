package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface RecommendationStorage {
    Integer getCountLikes(Long userId, Long userForCompareId);
    List<Film> getLikedFilms(Long userId, Long userToRecommendationId);
}
