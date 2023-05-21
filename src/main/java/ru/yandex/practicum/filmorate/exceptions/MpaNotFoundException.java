package ru.yandex.practicum.filmorate.exceptions;

public class MpaNotFoundException extends RuntimeException {

    public MpaNotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public MpaNotFoundException(final String message) {
        super(message);
    }
}
