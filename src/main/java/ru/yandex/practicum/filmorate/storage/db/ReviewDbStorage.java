package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Primary
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewMapper reviewMapper;

    @Override
    public Review create(Review review) {
        userStorage.checkUser(review.getUserId());
        filmStorage.checkFilm(review.getFilmId());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
            .withTableName("REVIEWS")
            .usingGeneratedKeyColumns("REVIEW_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("FILM_ID", review.getFilmId());
        values.put("USER_ID", review.getUserId());
        values.put("DESCRIPTION", review.getContent());
        values.put("IS_POSITIVE", review.getIsPositive());

        review.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        checkReview(review.getId());
        userStorage.checkUser(review.getUserId());
        filmStorage.checkFilm(review.getFilmId());
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("grade", review.getIsPositive());
        parameterSource.addValue("desc", review.getContent());
        parameterSource.addValue("rid", review.getId());
        String sql = "UPDATE REVIEWS SET IS_POSITIVE = :grade, DESCRIPTION = :desc WHERE REVIEW_ID = :rid";
        jdbcTemplate.update(sql, parameterSource);
        return read(review.getId());
    }

    @Override
    public Review read(Long id) {
        checkReview(id);
        String sqlGetReviewById = "SELECT R.*, COALESCE(G.USEFUL, 0) AS USEFUL " +
            "FROM REVIEWS R LEFT JOIN (SELECT REVIEW_ID, COUNT(CASE WHEN IS_POSITIVE = true THEN 1 END) " +
            "- COUNT(CASE WHEN IS_POSITIVE = false THEN 1 END) as USEFUL FROM GRADES GROUP BY REVIEW_ID) G " +
            "ON R.REVIEW_ID = G.REVIEW_ID WHERE R.REVIEW_ID = :id";
        SqlParameterSource parameters = new MapSqlParameterSource("id", id);
        List<Review> result = jdbcTemplate.query(sqlGetReviewById, parameters, new ReviewMapper());
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Review not found");
        }
        return result.get(0);
    }

    @Override
    public List<Review> read(Set<Long> id) {
        if (id.size() == 0) {
            return List.of();
        }
        String sqlGetAllReviewsBySetId = "SELECT R.*, COALESCE(G.USEFUL, 0) AS USEFUL " +
            "FROM REVIEWS R LEFT JOIN (SELECT REVIEW_ID, COUNT(CASE WHEN IS_POSITIVE = true THEN 1 END) " +
            "- COUNT(CASE WHEN IS_POSITIVE = false THEN 1 END) as USEFUL FROM GRADES GROUP BY REVIEW_ID) G " +
            "ON R.REVIEW_ID = G.REVIEW_ID WHERE R.REVIEW_ID IN (:ids) ORDER BY USEFUL DESC";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("ids", id);
        return jdbcTemplate.query(sqlGetAllReviewsBySetId, parameterSource, reviewMapper);
    }

    @Override
    public List<Review> readAll() {
        String sqlGetAllReviews = "SELECT R.*, COALESCE(G.USEFUL, 0) AS USEFUL " +
            "FROM REVIEWS R LEFT JOIN (SELECT REVIEW_ID, COUNT(CASE WHEN IS_POSITIVE = true THEN 1 END) " +
            "- COUNT(CASE WHEN IS_POSITIVE = false THEN 1 END) as USEFUL FROM GRADES GROUP BY REVIEW_ID) G " +
            "ON R.REVIEW_ID = G.REVIEW_ID ORDER BY USEFUL DESC ";
        return jdbcTemplate.query(sqlGetAllReviews, reviewMapper);
    }

    @Override
    public void delete(Long id) {
        checkReview(id);
        String sqlDeleteReviewFromGradesTable = "DELETE FROM REVIEWS WHERE REVIEW_ID = ?";
        jdbcTemplate.getJdbcTemplate().update(sqlDeleteReviewFromGradesTable, id);
    }

    @Override
    public void addLike(Long id, Long userId) {
        checkReview(id);
        userStorage.checkUser(userId);
        String sqlInsertGrade = "INSERT INTO GRADES (REVIEW_ID,USER_ID,IS_POSITIVE) VALUES (?,?,?)";
        jdbcTemplate.getJdbcTemplate().update(sqlInsertGrade, id, userId, true);
    }

    @Override
    public void addDislike(Long id, Long userId) {
        checkReview(id);
        userStorage.checkUser(userId);
        String sqlInsertGrade = "INSERT INTO GRADES (REVIEW_ID,USER_ID,IS_POSITIVE) VALUES (?,?,?)";
        jdbcTemplate.getJdbcTemplate().update(sqlInsertGrade, id, userId, false);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        checkReview(id);
        userStorage.checkUser(userId);
        String sqlDeleteGrade = "DELETE FROM GRADES WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_POSITIVE = true";
        jdbcTemplate.getJdbcTemplate().update(sqlDeleteGrade, id, userId);
    }

    @Override
    public void deleteDislike(Long id, Long userId) {
        checkReview(id);
        userStorage.checkUser(userId);
        String sqlDeleteGrade = "DELETE FROM GRADES WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_POSITIVE = false";
        jdbcTemplate.getJdbcTemplate().update(sqlDeleteGrade, id, userId);
    }

    @Override
    public List<Review> getAllReviewsByFilmId(Long id, Integer count) {
        filmStorage.checkFilm(id);
        String sqlGetAllJoinUseful =
            "SELECT R.*, COALESCE(G.USEFUL, 0) AS USEFUL " +
                "FROM REVIEWS R LEFT JOIN (SELECT REVIEW_ID, COUNT(CASE WHEN IS_POSITIVE = true THEN 1 END) " +
                "- COUNT(CASE WHEN IS_POSITIVE = false THEN 1 END) as USEFUL FROM GRADES GROUP BY REVIEW_ID) G " +
                "ON R.REVIEW_ID = G.REVIEW_ID WHERE FILM_ID = :id ORDER BY USEFUL DESC LIMIT :lim";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id", id);
        parameterSource.addValue("lim", count);
        return jdbcTemplate.query(sqlGetAllJoinUseful, parameterSource, reviewMapper);
    }

    private void checkReview(Long id) {
        String sqlGetReviewById = "SELECT * FROM REVIEWS WHERE REVIEW_ID = ?";
        SqlRowSet sqlReviewByIdRowSet = jdbcTemplate.getJdbcTemplate().queryForRowSet(sqlGetReviewById, id);
        if (!sqlReviewByIdRowSet.next()) {
            throw new ObjectNotFoundException("Review not found");
        }
    }
}
