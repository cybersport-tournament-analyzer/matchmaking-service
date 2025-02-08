package com.vkr.matchmaking_service.dto.user;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String steamId;
    private String username;
    private Long hoursPlayed;
    private Long ratingElo;
    private Long faceitWinrate;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Role role;
}
