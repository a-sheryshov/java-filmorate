package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.entity.AppError;
import ru.yandex.practicum.filmorate.entity.ErrorResponse;
import ru.yandex.practicum.filmorate.entity.ValidationErrorResponse;
import ru.yandex.practicum.filmorate.entity.Violation;
import ru.yandex.practicum.filmorate.exception.DirectorAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.WrongParameterException;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandlingControllerAdvice {
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(
            ConstraintViolationException e
    ) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> {
                            String name = "";
                            for (Path.Node node : violation.getPropertyPath()) {
                                name = node.getName();
                            }
                            return new Violation(name,
                                    violation.getMessage()
                            );
                        }
                )
                .collect(Collectors.toList());
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Constraint validation exception: ");
        violations.forEach(violation -> logMessage.append(violation.getMessage()).append(", "));
        log.error(logMessage.toString());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("MethodArgumentNotValid exception: ");
        violations.forEach(violation -> logMessage.append(violation.getMessage()).append(", "));
        log.error(logMessage.toString());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AppError onUserAlreadyExistsException(
            UserAlreadyExistsException e
    ) {
        log.error(e.getMessage());
        return new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public AppError onObjectNotFoundError(
            ObjectNotFoundException e
    ) {
        log.error(e.getMessage());
        return new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(DirectorAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AppError onDirectorAlreadyExistsError(
            DirectorAlreadyExistsException e
    ) {
        log.error(e.getMessage());
        return new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(WrongParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AppError onWrongParameterError(
            WrongParameterException e
    ) {
        log.error(e.getMessage());
        return new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable e) {
        e.printStackTrace();
        return new ErrorResponse("Unknown exception");
    }


}