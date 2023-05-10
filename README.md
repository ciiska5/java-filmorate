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
FROM films 
WHERE film_id IN (SELECT film_id 
                  FROM likes 
                  GROUP BY film_id 
                  ORDER BY COUNT(user_id) 
                  DESC LIMIT 5);
```

Получение общих друзей для `user_id_from = 1` и `user_id_from = 2`:
```

SELECT *
FROM users
WHERE user_id IN (SELECT two_users_friends.user_id_to AS mutual_friends
                  FROM (SELECT user_id_to
                        FROM friendship_status
                        WHERE user_id_from = 1 AND are_friends IS TRUE
                        UNION ALL
                        SELECT user_id_to
                        FROM friendship_status
                        WHERE user_id_from = 2 AND are_friends IS TRUE) AS two_users_friends
                  GROUP BY mutual_friends
                  HAVING COUNT(mutual_friends) > 1
                  );
```
