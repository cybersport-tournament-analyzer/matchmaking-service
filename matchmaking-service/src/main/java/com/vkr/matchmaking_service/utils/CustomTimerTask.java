package com.vkr.matchmaking_service.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.TimerTask;

@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
@Builder
public class CustomTimerTask extends TimerTask {
    private Runnable task;

    @Override
    public void run() {
        task.run();
    }
}
