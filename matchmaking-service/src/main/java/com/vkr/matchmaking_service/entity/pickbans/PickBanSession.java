package com.vkr.matchmaking_service.entity.pickbans;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class PickBanSession {

    private List<String> maps = new ArrayList<>(List.of("de_dust2", "de_inferno", "de_mirage", "de_nuke", "de_ancient", "de_train", "de_anubis"));
    private List<String> sides = new ArrayList<>(List.of("T", "CT"));
    private List<PickBanAction> actionsLogs = new ArrayList<>();
    private List<String> pickedMaps = new ArrayList<>();
    private List<SideSelection> sideSelections = new ArrayList<>();

    private String format;
    private UUID lobbyId;
    private String currentTeamTurn;
    private Action nextActionType;
    private boolean completed = false;

}
