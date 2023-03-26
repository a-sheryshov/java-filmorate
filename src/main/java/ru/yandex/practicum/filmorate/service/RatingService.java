package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

@Service
public class RatingService extends AbstractModelService<Rating, RatingStorage> implements ModelService<Rating> {

    @Autowired
    public RatingService(RatingStorage storage) {
        super(storage);
    }

}