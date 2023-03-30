package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Event extends AbstractModel {

    @NotNull
    private long timestamp;
    @NotNull
    private long userId;
    @NotNull
    private EventValue eventType;//   LIKE, REVIEW, FRIEND
    @NotNull
    private OperationValue operation;//   REMOVE, ADD, UPDATE
    @NotNull
    private long entityId;// идентификатор сущности, с которой произошло событие

}
