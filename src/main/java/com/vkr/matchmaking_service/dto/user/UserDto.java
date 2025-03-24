package com.vkr.matchmaking_service.dto.user;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@Getter
@Setter
public class UserDto {

    private String steamId;
    private String steamUsername;
    private Long ratingElo;
    private String avatarImageLink;
    private String steamProfileLink;
    private boolean ready = false;
    private boolean captain = false;
    private Role role;
}
