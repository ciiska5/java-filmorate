package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FriendshipStatus {
    private final int userFrom; // пользователь, отправивший запрос на дружбу пользователю userTo
    private final int userTo; // пользователь, получивший запрос на дружбу от пользователя userFrom
    private final boolean areFriends; //статус дружбы между пользователями userFrom и userTo
}
