package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Film extends AbstractModel {
    private static final String CINEMA_CREATION_DATE_STR = "1895-12-28";
    private static final LocalDate CINEMA_CREATION_DATE = LocalDate.parse(CINEMA_CREATION_DATE_STR);
    public static final int MAX_DESCRIPTION_LEN = 200;
    private static final String DESC_LEN_VALIDATION_ERR_MSG =
            "Description should be less than " + MAX_DESCRIPTION_LEN + " symbols";

    @NotBlank(message = "Name is mandatory")
    private String name;
    @Size(max = MAX_DESCRIPTION_LEN, message = DESC_LEN_VALIDATION_ERR_MSG)
    private String description;
    @NotNull(message = "Name is mandatory")
    private LocalDate releaseDate;
    @Positive(message = "Should be greater than 0")
    private int duration;
    @NotNull
    private Rating mpa = new Rating();
    @NotNull
    private Set<Genre> genres = new TreeSet<>(Comparator.comparing(g -> g.id));
    @NotNull
    private final Set<Long> likes = new HashSet<>();

    @AssertTrue(message = "Should be after " + CINEMA_CREATION_DATE_STR)
    private boolean isValidReleaseDate() {
        return !releaseDate.isBefore(CINEMA_CREATION_DATE);
    }

}
