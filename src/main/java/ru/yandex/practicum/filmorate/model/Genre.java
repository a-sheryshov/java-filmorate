package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Genre extends AbstractModel {
    @Size(max = 30)
    private String name = "";

    public Genre(Long id, String genreName) {
        this.id = id;
        this.name = genreName;
    }
}