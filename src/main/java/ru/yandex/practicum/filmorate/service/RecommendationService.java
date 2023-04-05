package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class RecommendationService {
    private UserDbStorage userDbStorage;
    private RecommendationStorage recommendationStorage;

    public List<Film> getRecommendations(Long userId) {
        userDbStorage.checkUser(userId);
        List<Film> recommendationFilms = new ArrayList<>();
        Optional<User> userToRecommendation = Optional.ofNullable(getUserWithMostTotalLikes(userId));
        if (userToRecommendation.isPresent()) {
            return recommendationStorage.getLikedFilms(userId, userToRecommendation.get().getId());
        } else {
            log.info("There are no recommended movies for user with id {}", userId);
            return recommendationFilms;
        }
    }

    private User getUserWithMostTotalLikes(Long userId) {
        Long userToRecommendationId = recommendationStorage.getUserIdWithMaxLikes(userId);
        if (userToRecommendationId == null) return null;
        return userDbStorage.read(userToRecommendationId);   
    }
}
