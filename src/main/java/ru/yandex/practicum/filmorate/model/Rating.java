package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
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