package ru.yandex.practicum.filmorate.service;

import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.AbstractModel;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Validated
public interface ModelService<E extends AbstractModel> {
    List<E> readAll();

    E read(@Valid @Positive final Long id);

    E create(@Valid final E data);

    E update(@Valid final E data);

}
