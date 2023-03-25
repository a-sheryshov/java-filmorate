package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@ToString
public class Rating extends AbstractModel {
    @Size(max = 10)
    private String name;

    public Rating(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Rating(Long id) {
        this.id = id;
        this.name = "";
    }

    public Rating() {
        this.name = "";
    }

}