package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ValidationException extends Exception {
    public ValidationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public ValidationException(final String message) {
        super(message);
    }
}
