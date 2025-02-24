package com.vkr.matchmaking_service.dto.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vkr.matchmaking_service.entity.server.Server;
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
public class ServerConfigDto {

    private String server_id; //required only

    private String added_voice_server = null;
    private Boolean autostop = false;
    private Integer autostop_minutes = 60;
    private Boolean confirmed = true;
    private String custom_domain = "";
    private Boolean deletion_protection = false;
    private Boolean enable_core_dump = false;
    private Boolean enable_mysql = false;
    private Boolean enable_syntropy = false;
    private String location = "helsinki";
    private Double manual_sort_order = 1731533425.0;
    private Integer max_disk_usage_gb = 30;
    private String name = "Counter-Strike 2 Server";
    private Boolean prefer_dedicated = true;
    private Boolean reboot_on_crash = false;
    private String scheduled_commands = "";
    private String server_image = "default";
    private String user_data = "";

    private Object ark_settings = null;
    private Server.Cs2Settings cs2_settings;
    private Object csgo_settings = null;
    private Object teamfortress2_settings = null;
    private Object teamspeak3_settings = null;
    private Object valheim_settings = null;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Cs2Settings {
        private int slots = 11;
        private String steam_game_server_login_token = null;
        private String rcon = "tmp4rv6bd";
        private String password = "";
        private String maps_source = "mapgroup";
        private String mapgroup = "";
        private String mapgroup_start_map = "";
        private String workshop_collection_id = "";
        private String workshop_collection_start_map_id = "";
        private String workshop_single_map_id = "";
        private boolean insecure = false;
        private boolean enable_gotv = true;
        private boolean enable_gotv_secondary = false;
        private boolean disable_bots = false;
        private String game_mode = "";
        private boolean enable_metamod = true;
        private List<String> metamod_plugins = List.of("654a32ea452c94f085961b91");
        private boolean private_server = false;
    }
}
