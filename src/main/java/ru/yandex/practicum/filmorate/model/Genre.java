package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@ToString
public class Genre extends AbstractModel {
    @Size(max = 30)
    private String name;

    public Genre(Long id) {
        this.id = id;
        this.name = "";
    }

    public Genre() {
        this.name = "";
    }
}