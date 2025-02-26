package com.vkr.matchmaking_service.entity.pickbans;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SideSelection {
    private String side;
    private String team;
}
