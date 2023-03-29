package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.RecommendationDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {
//    UserService userService;
//    public RecommendationService(UserService userService) {
//        this.userService = userService;
//    }
@Autowired
    private UserDbStorage userDbStorage;
@Autowired
    private RecommendationDbStorage recommendationDbStorage;
    public RecommendationService(UserDbStorage userDbStorage, RecommendationDbStorage recommendationDbStorage) {
        this.userDbStorage = userDbStorage;
        this.recommendationDbStorage = recommendationDbStorage;
    }




    public List<Film> getRecommendations(Long userId) {
      //  List<User> allUsers = userDbStorage.readAll();
        Integer count = recommendationDbStorage.getCountLikes(1L, 2L);
        return null;
    }

    private Integer getCountLikes(Long userId, Long userForCompareId) {
      return recommendationDbStorage.getCountLikes(userId, userForCompareId);
    }

}
