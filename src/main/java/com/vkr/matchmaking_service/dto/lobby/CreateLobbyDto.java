package com.vkr.matchmaking_service.dto.lobby;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@NoArgsConstructor
@Jacksonized
@AllArgsConstructor
public class CreateLobbyDto {
    private String mode;
    private String steamId;
    private String format;
    private String tournamentMatchId;
}
