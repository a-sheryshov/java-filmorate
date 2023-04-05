package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;


import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
@Slf4j
@AllArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {
    private NamedParameterJdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;
    DataSource dataSource;

    public Long getUserIdWithMaxLikes(Long userId) {
        String sql = "SELECT user_id " +
                "FROM films_likes " +
                "WHERE user_id <> ? " +
                "AND film_id IN ( " +
                "    SELECT film_id " +
                "    FROM films_likes " +
                "    WHERE user_id = ? " +
                ") " +
                "GROUP BY user_id " +
                "ORDER BY COUNT(film_id) DESC " +
                "LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setLong(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("user_id");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute query: " + e.getMessage(), e);
        }

    }

    @Override
    public List<Film> getLikedFilms(Long userId, Long userToRecommendationId) {

        String sql = "SELECT FILM_ID" +
                " FROM FILMS_LIKES" +
                " WHERE USER_ID = ?" +
                " EXCEPT " +
                "SELECT FILM_ID" +
                " FROM FILMS_LIKES" +
                " WHERE USER_ID = ?";

        List<Long> filmIds = jdbcTemplate.getJdbcTemplate().queryForList(sql, Long.class, userToRecommendationId, userId);
        Set<Long> ids = new HashSet<>(filmIds);
        return filmDbStorage.read(ids);
    }
}
