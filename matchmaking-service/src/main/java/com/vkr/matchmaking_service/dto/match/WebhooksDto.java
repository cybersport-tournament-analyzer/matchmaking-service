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
public class WebhooksDto {
    private String match_end_url;
    private String round_end_url;
    private String player_votekick_success_url;
    private String event_url;
    private List<String> enabled_events;
    private String authorization_header;
}
