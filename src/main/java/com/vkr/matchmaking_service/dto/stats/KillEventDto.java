package com.vkr.matchmaking_service.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@Builder
public class KillEventDto {
    private String killerName;
    private String killerSteamId;
    private String killerTeam;

    private String victimName;
    private String victimSteamId;
    private String victimTeam;

    private String weapon;
    private boolean headshot;
    private boolean penetrated;
    private boolean noscope;
    private boolean smoke;
}
