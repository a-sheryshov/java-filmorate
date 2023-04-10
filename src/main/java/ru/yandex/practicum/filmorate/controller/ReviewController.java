package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Validated
@Slf4j
public class ReviewController extends AbstractModelController<Review, ReviewService> {
    private static final Integer DEFAULT_VALUE_REVIEW_COUNT = 10;
    private static final String INFO_LOG_MSG_DEL = "Request 'DELETE' to '/reviews/{}', objectId: {}";

    @Autowired
    public ReviewController(ReviewService service) {
        super(service);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        log.info(INFO_LOG_MSG_DEL, id, id);
        service.deleteReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        service.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        service.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        service.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        service.deleteDislike(id, userId);
    }

    @GetMapping(params = {"filmId"})
    public List<Review> getAllReviewsByFilmId(@RequestParam(required = false) Long filmId) {
        if (filmId == null) {
            return super.readAll();
        } else {
            return service.getAllReviewsByFilmId(filmId, DEFAULT_VALUE_REVIEW_COUNT);
        }
    }

    @GetMapping(params = {"filmId", "count"})
    public List<Review> getAllReviewsByFilmIdLimitCounted(@RequestParam Long filmId,
                                                          @RequestParam @Positive Integer count) {
        return service.getAllReviewsByFilmId(filmId, count);
    }
}









