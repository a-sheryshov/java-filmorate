package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.CommonFilmsStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@AllArgsConstructor
public class CommonFilmsDbStorage implements CommonFilmsStorage {
    private NamedParameterJdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;

    public List<Film> getCommonFilms(Long userId, Long friendId) {

        String sql = "SELECT FILM_ID" +
                " FROM FILMS_LIKES" +
                " WHERE USER_ID = ?" +
                " AND " +
                "SELECT FILM_ID" +
                " FROM FILMS_LIKES" +
                " WHERE USER_ID = ?";

        List<Long> filmIds = jdbcTemplate.getJdbcTemplate().queryForList(sql, Long.class, friendId, userId);
        Set<Long> ids = new HashSet<>(filmIds);
        return filmDbStorage.read(ids);
    }
}
