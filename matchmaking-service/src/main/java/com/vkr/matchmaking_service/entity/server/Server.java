package com.vkr.matchmaking_service.entity.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Server {
        private String id;
        private long created_at;
        private String name;
        private String user_data;
        private String game;
        private String location;
        private int players_online;

        private List<ServerStatus> status;
        private boolean booting;
        private String server_error;
        private String ip;
        private String raw_ip;
        private String private_ip;
        private String match_id;
        private boolean on;
        private com.vkr.matchmaking_service.entity.server.Server.Ports ports;
        private boolean confirmed;
        private int max_disk_usage_gb;
        private double cost_per_hour;
        private double max_cost_per_hour;
        private double month_credits;
        private long month_reset_at;
        private double max_cost_per_month;
        private int subscription_cycle_months;
        private String subscription_state;
        private int subscription_renewal_failed_attempts;
        private long subscription_renewal_next_attempt_at;
        private int cycle_months_1_discount_percentage;
        private int cycle_months_3_discount_percentage;
        private int cycle_months_12_discount_percentage;
        private int first_month_discount_percentage;
        private boolean enable_mysql;
        private boolean autostop;
        private int autostop_minutes;
        private boolean enable_core_dump;
        private boolean prefer_dedicated;
        private boolean enable_syntropy;
        private String server_image;
        private boolean reboot_on_crash;
        private long manual_sort_order;
        private String mysql_username;
        private String mysql_password;
        private String ftp_password;
        private long disk_usage_bytes;
        private String default_file_locations;
        private String custom_domain;
        private List<String> scheduled_commands;
        private String added_voice_server;
        private String duplicate_source_server;
        private boolean deletion_protection;
        private boolean ongoing_maintenance;

        private com.vkr.matchmaking_service.entity.server.Server.Cs2Settings cs2_settings;

        private Object csgo_settings = null;
        private Object deadlock_settings = null;
        private Object minecraft_settings = null;
        private Object palworld_settings = null;
        private Object sevendaystodie_settings = null;
        private Object sonsoftheforest_settings = null;
        private Object soulmask_settings = null;
        private Object teamfortress2_settings = null;
        private Object teamspeak3_settings = null;
        private Object valheim_settings = null;
        private Object vrising_settings = null;


        @Data
        public static class Ports {
            private int game;
            private int gotv;
            private Integer gotv_secondary;
        }

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

