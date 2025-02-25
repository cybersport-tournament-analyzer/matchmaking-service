package com.vkr.matchmaking_service.dto.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vkr.matchmaking_service.entity.server.Server;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
@Builder
public class ServerSettingsDto {

    private String server_id;

    private Cs2Settings cs2_settings;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Cs2Settings {
        private int slots;
        private String steam_game_server_login_token;
        private String rcon;
        private String password;
        private String maps_source;
        private String mapgroup;
        private String mapgroup_start_map;
        private String workshop_collection_id;
        private String workshop_collection_start_map_id;
        private String workshop_single_map_id;
        private boolean insecure;
        private boolean enable_gotv;
        private boolean enable_gotv_secondary;
        private boolean disable_bots;
        private String game_mode;
        private boolean enable_metamod;
        private List<String> metamod_plugins;
        private boolean private_server;
    }
}
