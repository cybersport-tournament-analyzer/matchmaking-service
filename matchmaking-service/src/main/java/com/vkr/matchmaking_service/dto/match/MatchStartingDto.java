package com.vkr.matchmaking_service.dto.match;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchStartingDto {
    private String game_server_id; //required
    private MatchStartingTeamDto team1; //CT non-required
    private MatchStartingTeamDto team2; //T non-required
    private List<StartMatchPlayerDto> players; //required
    private MatchSettingsDto settings; //non-required
    private WebhooksDto webhooks; //non-required
}
