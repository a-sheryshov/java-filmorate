package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.AbstractModel;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Slf4j
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private final GenreMapper genreMapper;

    @Override
    public Film read(Long id) {
        String sql =
                "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME R_NAME " +
                        "FROM FILMS f JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                        "WHERE f.FILM_ID = :id";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        List<Film> result = jdbcTemplate.query(sql, parameterSource, filmMapper);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Film not found");
        }
        Film film = result.get(0);
        readLikes(film);
        readGenres(film);
        return result.get(0);
    }

    @Override
    public List<Film> read(Set<Long> idSet) {
        String sql =
                "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME R_NAME " +
                        "FROM FILMS f JOIN RATINGS r ON f.RATING_ID = r.RATING_ID WHERE f.FILM_ID IN (:ids) " +
                        "ORDER BY f.FILM_ID";
        SqlParameterSource parameterSource = new MapSqlParameterSource("ids", idSet);
        List<Film> result = jdbcTemplate.query(sql, parameterSource, filmMapper);
        readLikes(result);
        readGenres(result);
        return result;
    }

    @Override
    public List<Film> readAll() {
        String sql =
                "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME R_NAME " +
                        "FROM FILMS f JOIN RATINGS r ON f.RATING_ID = r.RATING_ID ORDER BY f.FILM_ID";
        List<Film> result = jdbcTemplate.query(sql, filmMapper);
        readLikes(result);
        readGenres(result);
        return result;
    }


    @Override
    public List<Film> getPopular(Integer limit) {
        String sql =
                "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME R_NAME\n" +
                        "FROM FILMS f " +
                        "JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                        "LEFT JOIN " +
                        "(SELECT FILM_ID, " +
                        "COUNT(USER_ID) AS cnt " +
                        "FROM FILMS_LIKES " +
                        "GROUP BY FILM_ID " +
                        ") l ON f.FILM_ID = l.FILM_ID " +
                        "ORDER BY l.cnt DESC " +
                        "LIMIT :lim";
        SqlParameterSource parameterSource = new MapSqlParameterSource("lim", limit);
        List<Film> result = jdbcTemplate.query(sql, parameterSource, filmMapper);
        readLikes(result);
        readGenres(result);
        return result;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("RELEASE_DATE", film.getReleaseDate());
        values.put("DURATION", film.getDuration());
        values.put("RATING_ID", film.getMpa().getId());
        values.put("GENRE_ID", film.getGenres());
        film.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        createGenresByFilm(film);
        readGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        checkFilm(film.getId());
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("name", film.getName());
        parameterSource.addValue("desc", film.getDescription());
        parameterSource.addValue("date", film.getReleaseDate());
        parameterSource.addValue("dur", film.getDuration());
        parameterSource.addValue("rid", film.getMpa().getId());
        parameterSource.addValue("fid", film.getId());

        String sql =
                "UPDATE FILMS SET NAME = :name, DESCRIPTION = :desc, RELEASE_DATE = :date, DURATION = :dur, " +
                        "RATING_ID = :rid WHERE FILM_ID = :fid";
        jdbcTemplate.update(sql, parameterSource);
        updateGenresByFilm(film);
        readGenres(film);
        return read(film.getId());
    }

    @Override
    public void saveLikes(Film film) {
        checkFilm(film.getId());
        jdbcTemplate.getJdbcTemplate().update("DELETE FROM FILMS_LIKES WHERE FILM_ID = ?", film.getId());
        String sql = "INSERT INTO FILMS_LIKES (FILM_ID, USER_ID) VALUES(?, ?)";
        List<Long> likes = new ArrayList<>(film.getLikes());
        jdbcTemplate.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, film.getId());
                ps.setLong(2, likes.get(i));
            }

            @Override
            public int getBatchSize() {
                return likes.size();
            }
        });
    }

    private void checkFilm(Long id) {
        String sql =
                "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME R_NAME " +
                        "FROM FILMS f JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                        "WHERE f.FILM_ID = ?";
        List<Film> result = jdbcTemplate.getJdbcTemplate().query(sql, filmMapper, id);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Film not found");
        }
    }

    private void checkFilm(List<Film> films) {
        Set<Long> ids = films.stream().map(AbstractModel::getId).collect(Collectors.toSet());
        String sql =
                "SELECT COUNT(*) AS COUNT FROM FILMS f WHERE f.FILM_ID IN (:ids)";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, parameters);
        if (result.next()) {
            if (result.getInt("COUNT") != ids.size())
                throw new ObjectNotFoundException("check id's");
            return;
        }
        throw new ObjectNotFoundException("check id's");
    }

    private void readLikes(Film film) {
        checkFilm(film.getId());
        String sql = "SELECT USER_ID FROM FILMS_LIKES WHERE FILM_ID = ? ORDER BY USER_ID ASC";
        SqlRowSet sqlRowSet = jdbcTemplate.getJdbcTemplate().queryForRowSet(sql, film.getId());
        while (sqlRowSet.next()) {
            film.getLikes().add(sqlRowSet.getLong("USER_ID"));
        }
    }

    private void readLikes(List<Film> films) {
        checkFilm(films);
        Set<Long> ids = films.stream().map(AbstractModel::getId).collect(Collectors.toSet());
        SqlParameterSource parameterSource = new MapSqlParameterSource("ids", ids);
        String sql = "SELECT FILM_ID, USER_ID FROM FILMS_LIKES WHERE FILM_ID IN (:ids) ORDER BY USER_ID ASC";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, parameterSource);
        while (sqlRowSet.next()) {
            films.stream()
                    .filter(film -> film.getId().equals(sqlRowSet.getLong("FILM_ID")))
                    .findFirst()
                    .ifPresentOrElse(film -> film.getLikes().add(sqlRowSet.getLong("USER_ID")),
                            () -> {
                            });
        }
    }

    private void readGenres(Film film) {
        checkFilm(film.getId());
        String sql = "SELECT g.GENRE_ID, g.NAME FROM GENRES g NATURAL JOIN FILMS_GENRES fg WHERE fg.FILM_ID = ?"
                + " ORDER BY GENRE_ID ASC";
        film.setGenres(new HashSet<>(jdbcTemplate.getJdbcTemplate().query(sql, genreMapper, film.getId())));
    }

    private void readGenres(List<Film> films) {
        checkFilm(films);
        Set<Long> ids = films.stream().map(AbstractModel::getId).collect(Collectors.toSet());
        SqlParameterSource parameterSource = new MapSqlParameterSource("ids", ids);
        String sql = "SELECT g.GENRE_ID, g.NAME, fg.FILM_ID FROM GENRES g NATURAL JOIN FILMS_GENRES fg WHERE fg.FILM_ID IN (:ids)"
                + " ORDER BY GENRE_ID ASC";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, parameterSource);
        while (sqlRowSet.next()) {
            Genre genre = new Genre();
            genre.setId(sqlRowSet.getLong("GENRE_ID"));
            genre.setName(sqlRowSet.getString("NAME"));
            films.stream()
                    .filter(film -> film.getId().equals(sqlRowSet.getLong("FILM_ID")))
                    .findFirst()
                    .ifPresentOrElse(film -> film.getGenres().add(genre), () -> {
                    });
        }
    }

    private void createGenresByFilm(Film film) {
        checkFilm(film.getId());
        String sql = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES(?, ?)";
        Set<Genre> genres = film.getGenres();
        if (genres == null) {
            return;
        }
        jdbcTemplate.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@NotNull PreparedStatement ps, int i) throws SQLException {
                Genre genre = new ArrayList<>(film.getGenres()).get(i);
                ps.setLong(1, film.getId());
                ps.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return film.getGenres().size();
            }
        });

    }

    private void updateGenresByFilm(Film film) {
        String sql = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.getJdbcTemplate().update(sql, film.getId());
        createGenresByFilm(film);
    }

    @Override
    public void delete(Long filmId) {

        String deleteLikesSql = "DELETE FROM films_likes WHERE film_id = :filmId";
        String deleteFilmSql = "DELETE FROM films WHERE film_id = :filmId";

        Map<String, Object> parameters = Collections.singletonMap("filmId", filmId);

        jdbcTemplate.update(deleteLikesSql, parameters);
        jdbcTemplate.update(deleteFilmSql, parameters);
    }

}