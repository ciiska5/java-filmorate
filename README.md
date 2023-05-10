# Filmorate

### Схема БД

Схема БД выглядит следуюющим образом:

![fimorate_DB_scheme](src/main/resources/filmorate_db.png)

Она состоит из 7 таблиц:
| Название таблицы | Описание |
| --- | --- |
| **films** | таблица с фильмами |
| **genres** | таблица с жанрами фильмов |
| **MPA_ratings** | таблица с рейтингами в соответствии с Motion Picture Association |
| **users** | таблица с пользователями |
| **film_genre** | таблица с фильмами и с соответствующими им жанрами |
| **likes** | таблица с фильмами и с пользователями, которым понравился соответствующий фильм |
| **friendship_status** | таблица со статусом дружбы между двумя пользователями, где `user_id_from` - пользователь, отправивший запрос на дружбу пользователю `user_id_to` |

### Примеры запросов

Получение всех фильмов:
`SELECT * FROM films;`

Получение всех пользователей:
`SELECT * FROM users;`

Получение топ-5 самых залайканных фильмов:
```
SELECT * 
FROM films AS f
JOIN MPA_ratings AS mr ON f.MPA_rating_id = mr.MPA_rating_id
LEFT JOIN (SELECT film_id,
                  COUNT(user_id) AS count
           FROM likes
           GROUP BY film_id) AS filmLikes
ON f.film_id = filmLikes.film_id
ORDER BY filmLikes.count DESC
LIMIT 5;
```

Получение общих друзей для `user_id = 1` и `user_id = 2`:
```
SELECT * 
FROM users AS u
WHERE u.user_id IN
                (SELECT user_id_to AS id FROM friendship_status WHERE user_id_from = 1
                UNION
                SELECT user_id_from AS id FROM friendship_status WHERE user_id_to = 1)
AND u.user_id IN
                (SELECT user_id_to AS id FROM friendship_status WHERE user_id_from = 2
                UNION
                SELECT user_id_from AS id FROM friendship_status WHERE user_id_to = 2;
```
