--СОЗДАНИЕ ТАБЛИЦ БАЗЫ ДАННЫХ--

DROP TABLE IF EXISTS users, friendship_status, films, MPA_ratings, genres, likes, film_genre, fk_mpa_ratings CASCADE;

--таблица с пользователями
CREATE TABLE IF NOT EXISTS users
(
	user_id          INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	email            VARCHAR NOT NULL,
	login			 VARCHAR NOT NULL,
	name             VARCHAR,
	birthday 		 DATE
);

--таблица со статусом дружбы между двумя пользователями,
--где user_id_from - пользователь, отправивший запрос на дружбу пользователю user_id_to
CREATE TABLE IF NOT EXISTS friendship_status
(
	user_id_from     INTEGER NOT NULL,
	user_id_to 		 INTEGER NOT NULL,
	are_friends      BOOLEAN NOT NULL,
	PRIMARY KEY (user_id_from, user_id_to),
	CONSTRAINT fk_users
		FOREIGN KEY (user_id_from)
		REFERENCES users (user_id) ON DELETE CASCADE
);

--таблица с рейтингами в соответствии с Motion Picture Association
CREATE TABLE IF NOT EXISTS MPA_ratings
(
	MPA_rating_id    INTEGER PRIMARY KEY,
	MPA_name         VARCHAR
);

--таблица с фильмами
CREATE TABLE IF NOT EXISTS films
(
	film_id          INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	MPA_rating_id    INTEGER,
	name 			 VARCHAR,
	description      VARCHAR(200),
	release_date     DATE,
	duration 		 INTEGER,
	CONSTRAINT fk_mpa_ratings
		FOREIGN KEY (MPA_rating_id)
		REFERENCES MPA_ratings (MPA_rating_id) ON DELETE CASCADE
);

--таблица с жанрами фильмов
CREATE TABLE IF NOT EXISTS genres
(
	genre_id      	 INTEGER NOT NULL PRIMARY KEY,
	name 		     VARCHAR NOT NULL
);

--таблица с фильмами и с пользователями, которым понравился соответствующий фильм
CREATE TABLE IF NOT EXISTS likes
(
	film_id      	 INTEGER,
	user_id 		 INTEGER,
	PRIMARY KEY (film_id, user_id),
	CONSTRAINT fk_to_film FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    CONSTRAINT fk_to_users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);

--таблица с фильмами и с соответствующими им жанрами
CREATE TABLE IF NOT EXISTS film_genre
(
	film_id      	 INTEGER NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
	genre_id 		 INTEGER NOT NULL REFERENCES genres (genre_id) ON DELETE RESTRICT,
	PRIMARY KEY (film_id, genre_id)
);