package com.vkr.matchmaking_service.entity.pickbans;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Action {
    BAN("BAN"),
    PICK("PICK"),
    PICK_SIDE("PICK_SIDE");

    private final String action;

    @Override
    public String toString() {
        return action;
    }

    public static Action fromString(String action) {

        if (action == null || action.isEmpty()) {
            return null;
        }

        for (Action action1 : Action.values()) {
            if (action1.name().equalsIgnoreCase(action) || action1.action.equalsIgnoreCase(action)) {
                return action1;
            }
        }

        throw new IllegalArgumentException("No such action: " + action);
    }
}
