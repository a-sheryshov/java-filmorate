package ru.yandex.practicum.filmorate.exception;

public class WrongParameterException extends RuntimeException {
    public WrongParameterException(String message) {
        super(message);
    }
}