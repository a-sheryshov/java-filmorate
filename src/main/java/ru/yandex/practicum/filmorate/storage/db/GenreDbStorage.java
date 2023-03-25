package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Primary
public class GenreDbStorage implements GenreStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre read(Long id) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        List<Genre> result = jdbcTemplate.getJdbcTemplate().query(sql, new GenreMapper(), id);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Genre not found");
        }
        return result.get(0);
    }

    @Override
    public List<Genre> read(Set<Long> id_set) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", id_set);
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID IN (:ids) ORDER BY GENRE_ID";
        List<Genre> result = jdbcTemplate.getJdbcTemplate().query(sql, new GenreMapper(), parameters);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Genre not found");
        }
        return result;
    }

    @Override
    public List<Genre> readAll() {
        String sql = "SELECT * FROM GENRES ORDER BY GENRE_ID";
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    @Override
    public Genre create(Genre genre) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("GENRES")
                .usingGeneratedKeyColumns("GENRE_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("NAME", genre.getName());

        genre.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        return genre;
    }

    @Override
    public Genre update(Genre genre) {
        String sql = "UPDATE GENRES SET NAME = :name WHERE GENRE_ID = :gid";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("name", genre.getName());
        parameterSource.addValue("gid", genre.getId());
        jdbcTemplate.update(sql, parameterSource);
        return genre;
    }
}
