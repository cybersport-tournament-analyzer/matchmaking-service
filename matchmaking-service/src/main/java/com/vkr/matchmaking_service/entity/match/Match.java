package com.vkr.matchmaking_service.entity.match;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Jacksonized
@Builder
public class Match {

    private String id;
    private String game_server_id;
    private Team team1;
    private Team team2;
    private List<Player> players;
    private Settings settings;
    private Webhooks webhooks;
    private int rounds_played;
    private boolean finished;
    private String cancel_reason;
    private List<Event> events;

    @Data
    public static class Team {
        private String name;
        private String flag;
        private Stats stats;
    }

    @Data
    public static class Stats {
        private int score;
    }

    @Data
    public static class Player {
        private String match_id;
        private String steam_id_64;
        private String team;
        private String nickname_override;
        private boolean connected;
        private boolean kicked;
        private String disconnected_at;
        private PlayerStats stats;
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

    @Data
    public static class Settings {
        private String map;
        private String password;
        private int connect_time;
        private int match_begin_countdown;
        private int team_size;
        private boolean wait_for_gotv;
        private boolean enable_plugin;
        private boolean enable_tech_pause;
    }

    @Data
    public static class Webhooks {
        private String match_end_url;
        private String round_end_url;
        private String player_votekick_success_url;
        private String event_url;
        private List<String> enabled_events;
        private String authorization_header;
    }

    @Data
    public static class Event {
        private String event;
        private long timestamp;
        private Payload payload;
    }

    @Data
    public static class Payload {
        private String steam_id_64;
    }
}
