package ru.yandex.practicum.filmorate.exceptions;

public class GenreNotFoundException extends RuntimeException {

    public GenreNotFoundException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public GenreNotFoundException(final String message) {
        super(message);
    }
}
