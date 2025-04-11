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
public class RoundEndReasonDto {
    private String winningTeam;
    private String reasonCode;
    private int scoreCT;
    private int scoreT;
}
