package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

@Service
@Slf4j
public class ReviewService extends AbstractModelService<Review, ReviewStorage> implements ModelService<Review> {

    @Autowired
    public ReviewService(ReviewStorage storage) {
        super(storage);
    }

    public void deleteReview(Long id) {
        storage.delete(id);
        log.info("{} deleted: {}", Review.class.getSimpleName(), id);
    }

    public void addLike(Long id, Long userId) {
        storage.addLike(id, userId);
        log.info("Like from {} added to review {}", userId, id);
    }

    public void addDislike(Long id, Long userId) {
        storage.addDislike(id, userId);
        log.info("Dislike from {} added to review {}", userId, id);
    }

    public void deleteLike(Long id, Long userId) {
        storage.deleteLike(id, userId);
        log.info("Like from {} deleted from review {}", userId, id);
    }

    public void deleteDislike(Long id, Long userId) {
        storage.deleteDislike(id, userId);
        log.info("Dislike from {} deleted from review {}", userId, id);
    }

    public List<Review> getAllReviewsByFilmId(Long id, Integer count) {
        return storage.getAllReviewsByFilmId(id, count);
    }
}


