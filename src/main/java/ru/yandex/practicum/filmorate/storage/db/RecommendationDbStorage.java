package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class RecommendationDbStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public Integer getCountLikes(Long userId, Long userForCompareId) {

        String sql = "SELECT COUNT(*) FROM FILMS_LIKES WHERE USER_ID IN (?, ?)" +
                " GROUP BY FILM_ID HAVING COUNT(DISTINCT USER_ID) = 2";

        SqlRowSet rows = jdbcTemplate.getJdbcTemplate().queryForRowSet(sql, userId, userForCompareId);
        int count = 0;
        if (rows.next()) {
            count = rows.getInt(1);
            System.out.println("Count: " + count);
        }
        return count;
    }

    public List<Film> getLikedFilms(Long userId) {
        return null;
    }

}
