package com.vkr.matchmaking_service.dto.lobby;

import com.vkr.matchmaking_service.entity.match.Match;
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
public class CreateMatchDto {
    private String mode;
    private String format;
    private Match match;
    private int team1Score = 0;
    private int team2Score = 0;
}
