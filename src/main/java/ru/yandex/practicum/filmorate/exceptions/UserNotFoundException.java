package ru.yandex.practicum.filmorate.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public UserNotFoundException(final String message) {
        super(message);
    }
}