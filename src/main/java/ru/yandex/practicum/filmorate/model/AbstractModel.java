package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Positive;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class AbstractModel {
    @Positive(message = "Should be greater than 0")
    Long id = 1L;

}
