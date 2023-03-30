package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Primary
@AllArgsConstructor
public class RatingDbStorage implements RatingStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RatingMapper ratingMapper;

    @Override
    public Rating read(Long id) {
        String sql = "SELECT * FROM RATINGS WHERE RATING_ID = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        List<Rating> result = jdbcTemplate.query(sql, parameterSource, ratingMapper);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Rating not found");
        }
        return result.get(0);
    }

    @Override
    public List<Rating> read(Set<Long> idSet) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", idSet);
        String sql = "SELECT * FROM RATINGS WHERE RATING_ID IN (:ids) ORDER BY RATING_ID";
        List<Rating> result = jdbcTemplate.query(sql, parameters, ratingMapper);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Genre not found");
        }
        return result;
    }

    @Override
    public List<Rating> readAll() {
        String sql = "SELECT * FROM RATINGS ORDER BY RATING_ID";
        return jdbcTemplate.query(sql, ratingMapper);
    }

    @Override
    public Rating create(Rating rating) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("RATINGS")
                .usingGeneratedKeyColumns("RATING_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("NAME", rating.getName());

        rating.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        return rating;
    }

    @Override
    public Rating update(Rating rating) {
        String sql = "UPDATE RATINGS SET NAME = :name WHERE RATING_ID = :id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("name", rating.getName());
        parameterSource.addValue("id", rating.getId());
        jdbcTemplate.update(sql, parameterSource);
        return rating;
    }
}
