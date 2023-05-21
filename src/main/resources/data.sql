--Инициализация первоначальных данных в базе

--заполнение таблиц с рейтингами фильмов
MERGE INTO MPA_ratings (MPA_rating_id, MPA_name) VALUES (1, 'G');
MERGE INTO MPA_ratings (MPA_rating_id, MPA_name) VALUES (2, 'PG');
MERGE INTO MPA_ratings (MPA_rating_id, MPA_name) VALUES (3, 'PG-13');
MERGE INTO MPA_ratings (MPA_rating_id, MPA_name) VALUES (4, 'R');
MERGE INTO MPA_ratings (MPA_rating_id, MPA_name) VALUES (5, 'NC-17');

--заполнение таблиц с жанрами фильмов
MERGE INTO genres (genre_id, name) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, name) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, name) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, name) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, name) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, name) VALUES (6, 'Боевик');