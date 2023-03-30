package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.db.RecommendationDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {
    private UserDbStorage userDbStorage;
    private RecommendationStorage recommendationStorage;

    @Autowired
    public RecommendationService(UserDbStorage userDbStorage, RecommendationDbStorage recommendationDbStorage) {
        this.userDbStorage = userDbStorage;
        this.recommendationStorage = recommendationDbStorage;
    }

    public List<Film> getRecommendations(Long userId) {
        List<Film> recommendationFilms = new ArrayList<>();
        User userToRecommendation = getUserWithMostTotalLikes(userId);
        if (userToRecommendation.getName() == null) {
            log.info("Для пользователя с id {} нет рекомендованных фильмов", userId);
            return recommendationFilms;
        }
        recommendationFilms = recommendationStorage.getLikedFilms(userId, userToRecommendation.getId());
        return recommendationFilms;
    }

    private User getUserWithMostTotalLikes(Long userId) {
        List<User> allUsers = userDbStorage.readAll();
        Integer maxCount = 0;
        User userToRecommendation = new User();

        for (User user : allUsers) {
            Integer count = recommendationStorage.getCountLikes(userId, user.getId());
            if (count > maxCount) {
                maxCount = count;
                userToRecommendation = user;
            }
        }
        return userToRecommendation;
    }
}
