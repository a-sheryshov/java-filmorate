package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventValue;
import ru.yandex.practicum.filmorate.model.OperationValue;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = new Event();
        event.setId(resultSet.getLong("EVENT_ID"));
        event.setTimestamp(resultSet.getLong("EVENT_TIMESTAMP"));
        event.setUserId(resultSet.getLong("USER_ID"));
        event.setEventType(EventValue.valueOf(resultSet.getString("EVENT_TYPE")));
        event.setOperation(OperationValue.valueOf(resultSet.getString("OPERATION")));
        event.setEntityId(resultSet.getLong("ENTITY_ID"));
        return event;
    }
}
