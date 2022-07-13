package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserFriendship {

    private Long firstUserId;

    private Long secondUserId;

    private boolean status;
}