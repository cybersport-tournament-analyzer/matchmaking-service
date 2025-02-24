package com.vkr.matchmaking_service.dto.match;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartMatchPlayerDto {
    private String steam_id_64;
    private String team; //team1, team2, spectator
    private String nickname_override;
}
