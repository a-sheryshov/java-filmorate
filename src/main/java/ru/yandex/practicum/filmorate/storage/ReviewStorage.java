package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage extends ModelStorage<Review> {
    void delete(Long id);

    void addLike(Long id, Long userId);

    void addDislike(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    void deleteDislike(Long id, Long userId);

    List<Review> getAllReviewsByFilmId(Long id, Integer count);
}
