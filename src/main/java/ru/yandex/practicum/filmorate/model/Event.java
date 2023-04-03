package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Event extends AbstractModel {

    @NotNull
    private long timestamp;
    @NotNull
    private long userId;
    @NotNull
    private EventValue eventType;
    @NotNull
    private OperationValue operation;
    @NotNull
    private long entityId;

    @Override
    @JsonProperty("eventId")
    public Long getId() {
        return this.id;
    }

}
