package com.vkr.matchmaking_service.dto.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ServerMetricsDto {

    @JsonProperty("all_time_players")
    private List<PlayerStats> allTimePlayers;

    @JsonProperty("players_online")
    private List<PlayerStats> playersOnline;

    @JsonProperty("players_online_graph")
    private List<OnlineGraphPoint> playersOnlineGraph;

    @Data
    public static class PlayerStats {
        private String name;
        private Integer duration;
        private Integer score;

        @JsonProperty("maps_played")
        private List<MapPlayed> mapsPlayed;
    }

    @Data
    public static class MapPlayed {
        private String map;
        private Integer seconds;
    }

    @Data
    public static class OnlineGraphPoint {
        private Long timestamp;
        private Integer value;
    }
}
