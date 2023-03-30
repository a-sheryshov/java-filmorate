package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.RecommendationStorage;

import java.util.*;

@Component
@Slf4j
@AllArgsConstructor
public class RecommendationDbStorage implements RecommendationStorage {
    private NamedParameterJdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;

    public Integer getCountLikes(Long userId, Long userForCompareId) {

        String sql = "SELECT COUNT(*)" +
                " FROM FILMS_LIKES" +
                " WHERE USER_ID IN (?, ?)" +
                " GROUP BY FILM_ID" +
                " HAVING COUNT(DISTINCT USER_ID) = 2";

        SqlRowSet rows = jdbcTemplate.getJdbcTemplate().queryForRowSet(sql, userId, userForCompareId);
        int count = 0;
        if (rows.next()) {
            count = rows.getInt(1);
        }
        return count;
    }

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
        List<Film> result = filmDbStorage.read(ids);
        return result;
    }
}
