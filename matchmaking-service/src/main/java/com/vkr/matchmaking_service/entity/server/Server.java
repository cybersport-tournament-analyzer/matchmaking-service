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

        private List<String> status;
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

        private Object csgo_settings;
        private Object deadlock_settings;
        private Object minecraft_settings;
        private Object palworld_settings;
        private Object sevendaystodie_settings;
        private Object sonsoftheforest_settings;
        private Object soulmask_settings;
        private Object teamfortress2_settings;
        private Object teamspeak3_settings;
        private Object valheim_settings;
        private Object vrising_settings;


        @Data
        public static class Ports {
            private int game;
            private int gotv;
            private Integer gotv_secondary;

        }

        @Data
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