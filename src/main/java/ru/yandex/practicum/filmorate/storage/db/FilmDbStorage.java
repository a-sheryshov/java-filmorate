package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film read(Long id) throws ObjectNotFoundException {
        String sql =
                "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME R_NAME " +
                        "FROM FILMS f JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                        "WHERE f.FILM_ID = ?";
        List<Film> result = jdbcTemplate.query(sql, this::mapToFilm, id);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Film not found");
        }
        Film film = result.get(0);
        readLikes(film);
        film.setGenres(readGenresByFilm(film));
        return result.get(0);
    }

    private void checkFilm(Long id) throws ObjectNotFoundException {
        String sql =
                "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME R_NAME " +
                        "FROM FILMS f JOIN RATINGS r ON f.RATING_ID = r.RATING_ID " +
                        "WHERE f.FILM_ID = ?";
        List<Film> result = jdbcTemplate.query(sql, this::mapToFilm, id);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Film not found");
        }
    }

    private Film mapToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("FILM_ID"));
        film.setName(resultSet.getString("NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));
        film.setReleaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate());
        film.setDuration(resultSet.getInt("DURATION"));
        film.setMpa(new Rating(resultSet.getLong("RATING_ID"), resultSet.getString("R_NAME")));
        return film;
    }

    @Override
    public List<Film> readAll() {
        String sql =
                "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.RATING_ID, r.NAME R_NAME " +
                        "FROM FILMS f JOIN RATINGS r ON f.RATING_ID = r.RATING_ID ORDER BY f.FILM_ID";
        List<Film> result = jdbcTemplate.query(sql, this::mapToFilm);
        for (Film film : result) {
            readLikes(film);
            film.setGenres(readGenresByFilm(film));
        }
        return result;
    }

    @Override
    public List<Film> getPopular(Integer count) {
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
                        "LIMIT ?";
        List<Film> result = jdbcTemplate.query(sql, this::mapToFilm, count);
        for (Film film : result) {
            readLikes(film);
            film.setGenres(readGenresByFilm(film));
        }
        return result;
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("RELEASE_DATE", film.getReleaseDate());
        values.put("DURATION", film.getDuration());
        values.put("RATING_ID", film.getMpa().getId());
        film.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        createGenresByFilm(film);
        return film;
    }

    @Override
    public Film update(Film film) throws ObjectNotFoundException {
        checkFilm(film.getId());
        String sql =
                "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? " +
                        "WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        updateGenresByFilm(film);
        return read(film.getId());
    }

    @Override
    public void saveLikes(Film film) throws ObjectNotFoundException {
        checkFilm(film.getId());
        jdbcTemplate.update("DELETE FROM FILMS_LIKES WHERE FILM_ID = ?", film.getId());

        String sql = "INSERT INTO FILMS_LIKES (FILM_ID, USER_ID) VALUES(?, ?)";
        Set<Long> likes = film.getLikes();
        for (var like : likes) {
            jdbcTemplate.update(sql, film.getId(), like);
        }
    }

    private void readLikes(Film film) {
        checkFilm(film.getId());
        String sql = "SELECT USER_ID FROM FILMS_LIKES WHERE FILM_ID = ? ORDER BY USER_ID ASC";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, film.getId());
        while (sqlRowSet.next()) {
            film.getLikes().add(sqlRowSet.getLong("USER_ID"));
        }
    }

    //@Override
    public Set<Genre> readGenresByFilm(Film film) throws ObjectNotFoundException {
        checkFilm(film.getId());
        String sql = "SELECT g.GENRE_ID, g.NAME FROM GENRES g NATURAL JOIN FILMS_GENRES fg WHERE fg.FILM_ID = ?"
                + " ORDER BY GENRE_ID ASC";
        return new HashSet<>(jdbcTemplate.query(sql, this::mapToGenre, film.getId()));
    }

    private Genre mapToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getLong("GENRE_ID"));
        genre.setName(resultSet.getObject("NAME").toString());
        return genre;
    }

    //@Override
    private void createGenresByFilm(Film film) throws ObjectNotFoundException {
        checkFilm(film.getId());
        String sql = "INSERT INTO FILMS_GENRES (FILM_ID, GENRE_ID) VALUES(?, ?)";
        Set<Genre> genres = film.getGenres();
        if (genres == null) {
            return;
        }
        for (var genre : genres) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }


    private void updateGenresByFilm(Film film) throws ObjectNotFoundException {
        String sql = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getId());
        createGenresByFilm(film);
    }


}