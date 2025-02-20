package com.vkr.matchmaking_service.entity.pickbans;

import lombok.*;

import java.util.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PickBanSession {

    private final List<String> maps = new ArrayList<>(List.of("de_dust2", "de_inferno", "de_mirage", "de_nuke", "de_overpass", "de_train", "de_anubis"));
    private final List<String> sides = new ArrayList<>(List.of("T", "CT"));
    private List<PickBanAction> actionsLogs = new ArrayList<>();
    private List<String> pickedMaps = new ArrayList<>();
    private Map<String, String> sideSelections = new HashMap<>();

    private String format;
    private UUID lobbyId;
    private String currentTeamTurn;
    private Action nextActionType;
    private int phaseNumber;
    private TimerTask currentTimer;
    private boolean completed = false;

}
