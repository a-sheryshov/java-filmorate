package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends AbstractModelController<User, UserService> {
    @Autowired
    public UserController(UserService service) {
        super(service);
    }

    @PutMapping("/{id}/friends/{userId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long userId) {
        service.addFriend(id, userId);
    }

    @DeleteMapping("/{id}/friends/{userId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long userId) {
        service.removeFriend(id, userId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{userId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long userId) {
        return service.getCommonFriends(id, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        service.delete(userId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getEvents(@PathVariable Long id) {
        return service.getEventsByUser(id);
    }

}
