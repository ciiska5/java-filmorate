package ru.yandex.practicum.filmorate.exceptions;

public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public FilmNotFoundException(final String message) {
        super(message);
    }
}
