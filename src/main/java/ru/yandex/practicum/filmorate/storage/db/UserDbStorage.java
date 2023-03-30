package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.AbstractModel;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final UserMapper userMapper;

    @Override
    public User read(Long id) throws ObjectNotFoundException {
        String sql = "SELECT * FROM USERS WHERE USER_ID = :uid";
        SqlParameterSource parameters = new MapSqlParameterSource("uid", id);
        List<User> result = jdbcTemplate.query(sql, parameters, new UserMapper());
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("User not found");
        }
        User user = result.get(0);
        readFriends(user);
        readLikes(user);
        return user;
    }

    @Override
    public List<User> read(Set<Long> idSet) {
        if (idSet.size() == 0) {
            return new ArrayList<>();
        }
        String sql = "SELECT * FROM USERS WHERE USER_ID IN (:ids) ORDER BY USER_ID";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource("ids", idSet);
        List<User> result = jdbcTemplate.query(sql, parameterSource, userMapper);
        for (Long id : idSet) {
            if (result.stream().filter(f -> f.getId().equals(id)).findFirst().isEmpty())
                throw new ObjectNotFoundException("User " + id + "not found");
        }
        readLikes(result);
        readFriends(result);
        return result;
    }

    @Override
    public List<User> readAll() {
        String sql = "SELECT * FROM USERS ORDER BY USER_ID";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public User create(User user) {
        if (containsEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("EMAIL", user.getEmail());
        values.put("LOGIN", user.getLogin());
        values.put("NAME", user.getName());
        values.put("BIRTHDAY", user.getBirthday());

        user.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        return user;
    }

    @Override
    public User update(User user) {
        checkUser(user.getId());
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("login", user.getLogin());
        parameterSource.addValue("email", user.getEmail());
        parameterSource.addValue("name", user.getName());
        parameterSource.addValue("birthday", user.getBirthday());
        parameterSource.addValue("uid", user.getId());
        String sql = "UPDATE USERS SET LOGIN = :login, EMAIL = :email, NAME = :name, BIRTHDAY = :birthday " +
                "WHERE USER_ID = :uid";
        jdbcTemplate.update(sql, parameterSource);
        return user;
    }

    @Override
    public boolean containsFriendship(Long filterId1, Long filterId2, Boolean filterConfirmed) {
        String sql = "SELECT * FROM FRIENDSHIP WHERE USER_ID1 = ? AND USER_ID2 = ? AND  IS_CONFIRMED = ?";
        SqlRowSet rows = jdbcTemplate.getJdbcTemplate().queryForRowSet(sql, filterId1, filterId2, filterConfirmed);
        return rows.next();
    }

    @Override
    public void updateFriendship(Long id1, Long id2, boolean confirmed, Long filterId1, Long filterId2) {
        String sql =
                "UPDATE FRIENDSHIP SET USER_ID1 = ?, USER_ID2 = ?, IS_CONFIRMED = ? " +
                        "WHERE USER_ID1 = ? AND USER_ID2 = ?";
        jdbcTemplate.getJdbcTemplate().update(sql, id1, id2, confirmed, filterId1, filterId2);
    }

    @Override
    public void insertFriendship(Long id, Long friendId) {
        String sql = "INSERT INTO FRIENDSHIP (USER_ID1, USER_ID2, IS_CONFIRMED) VALUES(:uid, :fr_id, :confirmed)";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("uid", id);
        parameterSource.addValue("fr_id", friendId);
        parameterSource.addValue("confirmed", false);
        jdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public void removeFriendship(Long filterId1, Long filterId2) {
        String sql = "DELETE FROM FRIENDSHIP WHERE USER_ID1 = ? AND USER_ID2 = ?";
        jdbcTemplate.getJdbcTemplate().update(sql, filterId1, filterId2);
    }

    private boolean containsEmail(String email) {
        String sql = "SELECT * FROM USERS WHERE EMAIL = ?";
        SqlRowSet filmRows = jdbcTemplate.getJdbcTemplate().queryForRowSet(sql, email);
        return filmRows.next();
    }

    private List<User> checkUser(Long id) {
        String sql = "SELECT * FROM USERS WHERE USER_ID = ?";
        List<User> result = jdbcTemplate.getJdbcTemplate().query(sql, userMapper, id);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("User not found");
        }
        return result;
    }

    private void checkUser(List<User> users) {
        Set<Long> ids = users.stream().map(AbstractModel::getId).collect(Collectors.toSet());
        String sql =
                "SELECT COUNT(*) AS COUNT FROM USERS u WHERE u.USER_ID IN (:ids)";
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, parameters);
        if (result.next()) {
            if (result.getInt("COUNT") != ids.size())
                throw new ObjectNotFoundException("check id's");
            return;
        }
        throw new ObjectNotFoundException("check id's");
    }

    private void readFriends(User user) {
        checkUser(user.getId());
        String sql =
                "(SELECT USER_ID2 ID FROM FRIENDSHIP  WHERE USER_ID1 = ?) " +
                        "UNION " +
                        "(SELECT USER_ID1 ID FROM FRIENDSHIP  WHERE USER_ID2 = ? AND  IS_CONFIRMED = true)";
        List<Long> friends = jdbcTemplate.getJdbcTemplate().queryForList(sql, Long.class,
                user.getId(), user.getId());
        user.getFriends().clear();
        for (Long id : friends) {
            user.getFriends().add(id);
        }
    }

    private void readFriends(List<User> users) {
        checkUser(users);
        Set<Long> ids = users.stream().map(AbstractModel::getId).collect(Collectors.toSet());
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id1", ids);
        parameterSource.addValue("id2", ids);
        String sql =
                "(SELECT USER_ID2 FRIEND_ID, USER_ID1 USER_ID FROM FRIENDSHIP  WHERE USER_ID1 IN (:id1)) " +
                        "UNION " +
                        "(SELECT USER_ID1 FRIEND_ID, USER_ID1 USER_ID FROM FRIENDSHIP " +
                        "WHERE USER_ID2 IN (:id2) AND  IS_CONFIRMED = true)";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, parameterSource);
        while (sqlRowSet.next()) {
            users.stream()
                    .filter(user -> user.getId().equals(sqlRowSet.getLong("USER_ID")))
                    .findFirst()
                    .ifPresentOrElse(user -> user.getFriends().add(sqlRowSet.getLong("FRIEND_ID")),
                            () -> {
                            });
        }
    }

    private void readLikes(User user) {
        checkUser(user.getId());
        String sql = "SELECT FILM_ID FROM FILMS_LIKES WHERE USER_ID = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.getJdbcTemplate().queryForRowSet(sql, user.getId());
        user.getLikes().clear();
        while (sqlRowSet.next()) {
            user.getLikes().add(sqlRowSet.getLong("FILM_ID"));
        }
    }

    private void readLikes(List<User> users) {
        checkUser(users);
        Set<Long> ids = users.stream().map(AbstractModel::getId).collect(Collectors.toSet());
        SqlParameterSource parameterSource = new MapSqlParameterSource("ids", ids);
        String sql = "SELECT FILM_ID, USER_ID FROM FILMS_LIKES WHERE USER_ID IN (:ids) ORDER BY FILM_ID ASC";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, parameterSource);
        while (sqlRowSet.next()) {
            users.stream()
                    .filter(user -> user.getId().equals(sqlRowSet.getLong("USER_ID")))
                    .findFirst()
                    .ifPresentOrElse(user -> user.getLikes().add(sqlRowSet.getLong("FILM_ID")),
                            () -> {
                            });
        }
    }

    @Override
    public void delete(Long userId) {
        User user = checkUser(userId).get(0);
        user.getFriends().forEach(idFriend -> removeFriendship(userId, idFriend));
        user.getLikes()
                .forEach(idLike -> {
                    String sql = String.format("DELETE FROM films_likes WHERE film_id = %s AND user_id = %s",

                            idLike, userId
                    );
                    jdbcTemplate.getJdbcTemplate().update(sql);
                });

        jdbcTemplate.getJdbcTemplate().update(
                String.format("DELETE FROM users WHERE user_id = %s", userId)
        );
    }
}