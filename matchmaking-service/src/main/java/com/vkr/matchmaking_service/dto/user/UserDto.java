package com.vkr.matchmaking_service.dto.user;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Embeddable
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

    @Enumerated(EnumType.STRING)
    private Role role;
}
