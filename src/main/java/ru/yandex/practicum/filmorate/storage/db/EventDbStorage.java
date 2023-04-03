package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@Primary
@AllArgsConstructor
public class EventDbStorage implements EventStorage {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final EventMapper eventMapper;

    @Override
    public Event read(Long eventId) {
        String sql = "SELECT * FROM EVENTS WHERE EVENT_ID = ?";
        List<Event> result = jdbcTemplate.getJdbcTemplate().query(sql, eventMapper, eventId);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("Event " + eventId + " not found");
        }
        return result.get(0);
    }

    @Override
    public List<Event> read(Set<Long> eventIds) {
        String sql = "SELECT * FROM EVENTS WHERE EVENT_ID IN (:ids)";
        SqlParameterSource parameterSource = new MapSqlParameterSource("ids", eventIds);
        List<Event> result = jdbcTemplate.query(sql, parameterSource, eventMapper);
        if (result.isEmpty()) {
            throw new ObjectNotFoundException("No events found");
        }
        return result;
    }

    public List<Event> readByUser(Long userId) {
        SqlParameterSource parameters = new MapSqlParameterSource("id", userId);
        String sql = "SELECT * FROM EVENTS WHERE USER_ID = :id ORDER BY EVENT_ID ASC";
        return jdbcTemplate.query(sql, parameters, eventMapper);
    }

    @Override
    public List<Event> readAll() {
        String sql = "SELECT * FROM EVENTS ORDER BY EVENT_ID";
        return jdbcTemplate.query(sql, eventMapper);
    }

    @Override
    public Event create(Event event) {
        Map<String, Object> values = new HashMap<>();
        values.put("EVENT_TIMESTAMP", event.getTimestamp());
        values.put("USER_ID", event.getUserId());
        values.put("EVENT_TYPE", event.getEventType().toString());
        values.put("OPERATION", event.getOperation().toString());
        values.put("ENTITY_ID", event.getEntityId());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate())
                .withTableName("events")
                .usingGeneratedKeyColumns("event_id");

        event.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        log.info("Event of user {} action {} created by id {}", event.getUserId(), event.getOperation(), event.getId());
        return event;
    }

    @Override
    public Event update(Event event) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("EVENT_TIMESTAMP", event.getTimestamp());
        parameterSource.addValue("USER_ID", event.getUserId());
        parameterSource.addValue("EVENT_TYPE", event.getEventType().toString());
        parameterSource.addValue("OPERATION", event.getOperation().toString());
        parameterSource.addValue("ENTITY_ID", event.getEntityId());
        parameterSource.addValue("EVENT_ID", event.getId());
        String sql = "UPDATE EVENTS SET EVENT_TIMESTAMP = :EVENT_TIMESTAMP, USER_ID = :USER_ID, " +
                "EVENT_TYPE = :EVENT_TYPE, OPERATION = :OPERATION, ENTITY_ID = :ENTITY_ID " +
                "WHERE EVENT_ID = :EVENT_ID";
        jdbcTemplate.update(sql, parameterSource);
        return event;
    }

}
