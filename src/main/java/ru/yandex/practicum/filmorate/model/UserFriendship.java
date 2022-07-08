package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFriendship {

    private Long firstUserId;
    private Long secondUserId;
    private boolean status;
}