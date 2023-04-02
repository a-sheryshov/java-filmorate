package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Director extends AbstractModel {
    @Pattern(regexp = "^[A-Z][a-zA-Z '.-]*[A-Za-z][^-]$",
            message = "Valid Characters include (A-Z) (a-z) (' space -)")
    private String name;

}