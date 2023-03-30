package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/users")
class RecommendationController {
    private final RecommendationService recommendationService;
    RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}/recommendations")
    public List<Film> getRecommendation(@PathVariable Long userId) {
        return recommendationService.getRecommendations(userId);
    }
}
