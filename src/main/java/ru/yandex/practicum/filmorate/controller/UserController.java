package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController extends AbstractController<User> {
    @PostMapping
    public User createUserAction(@Valid @RequestBody User user
            , HttpServletRequest request) {
        checkName(user);
        save(user);
        log.info(INFO_LOG_MSG_RGX,
                request.getMethod(), request.getRequestURI(), user.getId());
        return user;
    }

    @PutMapping
    public User updateUserAction(@Valid @RequestBody User user
            , HttpServletRequest request) {
        checkName(user);
        update(user);
        log.info(INFO_LOG_MSG_RGX,
                request.getMethod(), request.getRequestURI(), user.getId());
        return user;
    }

    @GetMapping
    public List<User> getAllUsersAction() {
        return getAll();
    }

    private void checkName(final User user) {
        Optional<String> optionalName = Optional.ofNullable(user.getName());
        optionalName.ifPresentOrElse((name) -> {
        }, () -> user.setName(user.getLogin()));
    }

}
