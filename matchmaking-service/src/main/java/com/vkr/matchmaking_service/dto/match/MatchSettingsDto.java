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
public class MatchSettingsDto {
    private String map; //de_dust2
    private String password = "";
    private Integer connect_time = 300; //default
    private Integer match_begin_countdown = 30; //default
    private Integer team_size = 5; //default
    private Boolean wait_for_gotv = false; //default (stop demo 20 sec after)
    private Boolean enable_plugin = false; //default (lock teams)
    private Boolean enable_tech_pause = false; //!tech to pause
}
