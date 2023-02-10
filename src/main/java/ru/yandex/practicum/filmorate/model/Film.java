package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class Film extends AbstractEntity {
    final LocalDate cinemaCreationDate = LocalDate.of(1895,12,28);
    @NotBlank(message = "Name is mandatory")
    String name;
    @Size(max = 200, message = "Description is too long")
    String description;
    @NotNull(message = "Name is mandatory")
    LocalDate releaseDate;
    @Positive(message = "Should be greater than 0")
    int duration;

    @AssertTrue(message = "Release date should be greater than 1895-12-28")
    private boolean isValidReleaseDate(){
        return !releaseDate.isBefore(cinemaCreationDate);
    }
}
