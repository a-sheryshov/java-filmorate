package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.*;

@Primary
@Component
public class DirectorDbStorage implements DirectorStorage {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private DirectorMapper directorMapper;

    @Override
    public Director read(Long id) {
        String sql = "SELECT * FROM directors WHERE director_id = :id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        List<Director> result = jdbcTemplate.query(sql, parameterSource, directorMapper);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Director not found");
        }
        return result.get(0);
    }

    @Override
    public List<Director> read(Set<Long> idSet) {
        String sql = "SELECT * FROM directors WHERE director_id = :id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("id", idSet);
        return jdbcTemplate.query(sql, parameterSource, directorMapper);
    }

    @Override
    public List<Director> readAll() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, directorMapper);
    }

    @Override
    public Director create(Director director) {
        checkNameNotExist(director);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("DIRECTOR_ID");
        Map<String, Object> values = new HashMap<>();
        values.put("NAME", director.getName());
        director.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        checkDirector(director.getId());
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("did", director.getId());
        parameterSource.addValue("name", director.getName());
        String sql = "UPDATE directors SET name = :name WHERE director_id = :did";
        jdbcTemplate.update(sql, parameterSource);
        return read(director.getId());
    }

    @Override
    public void delete(Long directorId) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("did", directorId);
        String sql = "DELETE from directors WHERE director_id = :did";
        jdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public Set<Director> readDirectorsByFilm(Long filmId) {
        String sql = "SELECT dir.director_id, dir.name " +
                "FROM films_directors AS fd " +
                "LEFT OUTER JOIN directors AS dir ON fd.director_id = dir.director_id " +
                "WHERE fd.film_id = :fid";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("fid", filmId);
        return new HashSet<>(jdbcTemplate.query(sql, parameterSource, directorMapper));
    }

    @Override
    public Map<Long, Set<Director>> readDirectorsByFilm(List<Long> filmIds) {
        Map<Long, Set<Director>> result = new HashMap<>();
        for (Long filmId : filmIds) {
            result.put(filmId, new HashSet<>());
        }
        SqlParameterSource parameterSource = new MapSqlParameterSource("ids", filmIds);
        String sql = "SELECT dir.director_id, dir.name, fd.film_id " +
                "FROM films_directors AS fd " +
                "LEFT OUTER JOIN directors AS dir ON fd.director_id = dir.director_id " +
                "WHERE fd.film_id IN (:ids) " +
                "ORDER BY fd.film_id ASC";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, parameterSource);
        while (sqlRowSet.next()) {
            Long filmId = sqlRowSet.getLong("film_id");
            Director director = new Director();
            director.setId(sqlRowSet.getLong("director_id"));
            director.setName(sqlRowSet.getString("name"));
            result.get(filmId).add(director);
        }
        return result;
    }

    @Override
    public void checkDirector(Long id) {
        String sql = "SELECT * FROM directors WHERE director_id = :id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        List<Director> result = jdbcTemplate.query(sql, parameterSource, directorMapper);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Director not found");
        }
    }

    private void checkNameNotExist(Director director) {
        String sql = "SELECT * FROM directors WHERE name = :name";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("name", director.getName());
        if (!jdbcTemplate.query(sql, parameterSource, directorMapper).isEmpty()) {
            throw new DirectorAlreadyExistsException("Director " + director.getName() +
                    " already exists");
        }
    }

}