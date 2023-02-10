package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractEntity {

    String name;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Wrong email format")
    String email;
    @NotBlank(message = "Login is mandatory")
    @Pattern(regexp = "\\S+", message = "Login should not contain spaces")
    String login;
    @Past(message = "Wrong birthday date")
    LocalDate birthday;

}
