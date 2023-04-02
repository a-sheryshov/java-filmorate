package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.CommonFilmsService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
@RequestMapping("/films/common")
class CommonFilmsController {
    private final CommonFilmsService commonFilmsService;

    public CommonFilmsController(CommonFilmsService commonFilmsService) {
        this.commonFilmsService = commonFilmsService;
    }

    @GetMapping()
    public List<Film> getCommonFilms(@RequestParam("userId") @NotNull @Positive Long userId,
                                     @RequestParam("friendId") @NotNull @Positive Long friendId) {

        return commonFilmsService.getCommonFilms(userId, friendId);
    }
}
