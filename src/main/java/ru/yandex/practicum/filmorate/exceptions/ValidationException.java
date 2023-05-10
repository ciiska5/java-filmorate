package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends RuntimeException {

    public ValidationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public ValidationException(final String message) {
        super(message);
    }
}
