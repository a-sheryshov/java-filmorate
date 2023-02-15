package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.AppError;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.entity.ValidationErrorResponse;
import ru.yandex.practicum.filmorate.entity.Violation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
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
            , HttpServletRequest request
    ) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("Constraint validation exception: ");
        violations.forEach(violation -> logMessage.append(violation.getMessage()+ ", "));
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
        violations.forEach(violation -> logMessage.append(violation.getMessage()+ ", "));
        log.error(logMessage.toString());
        return new ValidationErrorResponse(violations);
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

}