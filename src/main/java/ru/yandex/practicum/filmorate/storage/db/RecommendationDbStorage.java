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
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class RecommendationDbStorage {

    private NamedParameterJdbcTemplate jdbcTemplate;

//    public RecommendationDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }



public Integer getCountLikes(Long userId, Long userForCompareId) {
    //  SELECT COUNT(*) FROM FILMS_LIKES WHERE USER_ID IN (2, 1) GROUP BY FILM_ID HAVING COUNT(DISTINCT USER_ID) = 2;

   // String sql = "SELECT * FROM FRIENDSHIP WHERE USER_ID1 = ? AND USER_ID2 = ? AND  IS_CONFIRMED = ?";

    String sql = "SELECT COUNT(*) FROM FILMS_LIKES WHERE USER_ID IN (?, ?)" +
            " GROUP BY FILM_ID HAVING COUNT(DISTINCT USER_ID) = 2";

    SqlRowSet rows = jdbcTemplate.getJdbcTemplate().queryForRowSet(sql, userId, userForCompareId);
//    SqlRowSet rows = jdbcTemplate.getJdbcTemplate().queryForRowSet(sql, filterId1, filterId2, filterConfirmed
//    SqlParameterSource parameters = new MapSqlParameterSource("uid", "uid2", userId, userForCompareId);
//    List<User> result = jdbcTemplate.query(sql, parameters, new UserMapper());
//    if (result.isEmpty()) {
//        throw new ObjectNotFoundException("User not found");
//    }
    return Integer.parseInt(String.valueOf(rows.next()));
}

}
