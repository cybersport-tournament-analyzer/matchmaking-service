package com.vkr.matchmaking_service.dto.lobby;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddUserToLobbyDto {
    private String steamId;
    private String team;
}
