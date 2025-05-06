package com.vkr.matchmaking_service.dto.tournament_client.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class PlayerDto {
    private UUID id;
    private String playerSteamId;
    private InGameRole inGameRole;
    private boolean ready = false;
    private boolean captain = false;
    private String playerUsername;
}
