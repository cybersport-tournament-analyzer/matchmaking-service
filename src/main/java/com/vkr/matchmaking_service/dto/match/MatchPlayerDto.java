package com.vkr.matchmaking_service.dto.match;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vkr.matchmaking_service.entity.match.Match;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchPlayerDto {
    @Data
    public static class Player {
        private String match_id;
        private String steam_id_64;
        private String team;
        private String nickname_override;
        private boolean connected;
        private boolean kicked;
        private String disconnected_at;
        private Match.PlayerStats stats;
    }

    @Data
    public static class PlayerStats {
        private int kills;
        private int assists;
        private int deaths;
        private int mvps;
        private int score;
        private int _2ks;
        private int _3ks;
        private int _4ks;
        private int _5ks;
        private int kills_with_headshot;
        private int kills_with_pistol;
        private int kills_with_sniper;
        private int damage_dealt;
        private int entry_attempts;
        private int entry_successes;
        private int flashes_thrown;
        private int flashes_successful;
        private int flashes_enemies_blinded;
        private int utility_thrown;
        private int utility_damage;
        private int _1vX_attempts;
        private int _1vX_wins;
    }
}
