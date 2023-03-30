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
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
@AllArgsConstructor
public class RecommendationDbStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private FilmDbStorage filmDbStorage;

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

    public List<Film> getLikedFilms(Long userId, Long userToRecommendationId) {
      //  String sql = "SELECT FILM_ID FROM FILMS_LIKES WHERE USER_ID = ?";
        String sql = "SELECT FILM_ID FROM FILMS_LIKES WHERE USER_ID = ? MINUS SELECT FILM_ID FROM FILMS_LIKES WHERE USER_ID = ?";
//        SELECT column1, column2, column3
//        FROM table1
//        WHERE condition1
//        MINUS
//        SELECT column1, column2, column3
//        FROM table2
//        WHERE condition2;

        List<Long> filmIds = jdbcTemplate.getJdbcTemplate().queryForList(sql, Long.class, userToRecommendationId, userId);
        Set<Long> ids = new HashSet<>(filmIds);
        List<Film> result = filmDbStorage.read(ids);

        return result;

    }

}
