package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users")
class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping("/{userId}/recommendations")
    public List<Film> getRecommendation(@PathVariable @Validated Long userId) {
        return recommendationService.getRecommendations(userId);
    }
}
