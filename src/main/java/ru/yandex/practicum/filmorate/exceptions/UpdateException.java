package ru.yandex.practicum.filmorate.exceptions;

public class UpdateException extends RuntimeException {

    public UpdateException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public UpdateException(final String message) {
        super(message);
    }
}
