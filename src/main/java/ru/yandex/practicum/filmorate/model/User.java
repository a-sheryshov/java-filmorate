package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Getter
@RequiredArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractModel {
    @Setter
    private String name;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Wrong email format")
    private String email;
    @NotBlank(message = "Login is mandatory")
    @Pattern(regexp = "\\S+", message = "Login should not contain spaces")
    private String login;
    @Past(message = "Wrong birthday date")
    private LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();

    private final Set<Long> likes = new HashSet<>();

}
