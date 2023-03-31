package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;
import ru.yandex.practicum.filmorate.storage.db.RecommendationDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class RecommendationService {
    private UserDbStorage userDbStorage;
    private RecommendationStorage recommendationStorage;
    public List<Film> getRecommendations(Long userId) {
        userDbStorage.checkUser(userId);
        List<Film> recommendationFilms = new ArrayList<>();
        User userToRecommendation = getUserWithMostTotalLikes(userId);
        if (userToRecommendation.getName() == null) {
            log.info("There are no recommended movies for user with id {}", userId);
            return recommendationFilms;
        }
        recommendationFilms = recommendationStorage.getLikedFilms(userId, userToRecommendation.getId());
        return recommendationFilms;
    }

    private User getUserWithMostTotalLikes(Long userId) {
        List<User> allUsers = userDbStorage.readAll();
        if (allUsers.size() == 0) {
            throw new ObjectNotFoundException("Users not found");
        }
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
