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
    private static final LocalDate CINEMA_CREATION_DATE = LocalDate.of(1895,12,28);
    public static final int MAX_DESCRIPTION_LEN = 200;
    @NotBlank(message = "Name is mandatory")
    String name;
    @Size(max = MAX_DESCRIPTION_LEN, message = "Description is too long")
    String description;
    @NotNull(message = "Name is mandatory")
    LocalDate releaseDate;
    @Positive(message = "Should be greater than 0")
    int duration;

    @AssertTrue(message = "Release date can't be less than cinema creation date")
    private boolean isValidReleaseDate(){
        return !releaseDate.isBefore(CINEMA_CREATION_DATE);
    }
}
