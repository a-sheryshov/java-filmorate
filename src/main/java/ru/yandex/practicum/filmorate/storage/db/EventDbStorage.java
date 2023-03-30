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
            throw new ObjectNotFoundException("Event not found");
        }
        log.info("Найдено событие с ID: {}", eventId);
        return result.get(0);
    }

    @Override
    public List<Event> read(Set<Long> idSet) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", idSet);
        String sql = "SELECT * FROM EVENTS WHERE USER_ID IN (:ids) ORDER BY EVENT_ID DESC";
        List<Event> events = jdbcTemplate.query(sql, parameters, eventMapper);
        log.info("Найдено {} событий у пользователя с ID: {}", events.size(), idSet);
        return events;
    }

    @Override
    public List<Event> readAll() {
        String sql = "SELECT * FROM EVENTS ORDER BY EVENT_ID";
        List<Event> events = jdbcTemplate.query(sql, eventMapper);
        log.info("Всего событий: {}", events.size());
        return events;
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
        log.info("event add {}", event);
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

//    private final EventMapper eventMapper = new EventMapper();

//        String sql = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
//        List<Genre> result = jdbcTemplate.getJdbcTemplate().query(sql, new GenreMapper(), id);
//        if (result.isEmpty()) {
//            throw new ObjectNotFoundException("Genre not found");
//        }
//        return result.get(0);


//    public void createEvent(Long userId, EventValue eventType, OperationValue operation, Long entityId) {
//
//        Event event = new Event();
//        event.setTimestamp(new Date().getTime());
//        event.setUserId(userId);
//        event.setEventType(eventType);
//        event.setOperation(operation);
//        event.setEntityId(entityId);
//        System.out.println(event + "!!!!!!!!!!!!!!!!!!!!!creaate");
//
//        Map<String, Object> values = new HashMap<>();
//        values.put("EVENT_TIMESTAMP", new Date().getTime());
//        values.put("USER_ID", userId);
//        values.put("EVENT_TYPE", eventType.toString());
//        values.put("OPERATION", operation.toString());
//        values.put("ENTITY_ID", entityId);
//
//        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("events")
//                .usingGeneratedKeyColumns("event_id");
//
//        long ev = simpleJdbcInsert.executeAndReturnKey(values).longValue();
//        log.info("event add {}", ev);
//    }
//
//    public List<Event> getEvent(Long id) {
//        String sql = "SELECT * FROM EVENTS WHERE USER_ID = ? ORDER BY EVENT_TIMESTAMP DESC";
//        List<Event> events = jdbcTemplate.query(sql, eventMapper, id);
//        System.out.println(events);
//        return events;
//    }