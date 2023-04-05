package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();
        review.setId(resultSet.getLong("REVIEW_ID"));
        review.setFilmId(resultSet.getLong("FILM_ID"));
        review.setUserId(resultSet.getLong("USER_ID"));
        review.setContent(resultSet.getString("DESCRIPTION"));
        review.setIsPositive(resultSet.getBoolean("IS_POSITIVE"));
        review.setUseful(resultSet.getLong("USEFUL"));
        return review;
    }
}
