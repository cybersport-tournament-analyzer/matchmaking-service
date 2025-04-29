package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.redis.cache.match.MatchCache;
import com.vkr.matchmaking_service.redis.cache.series.SeriesCache;
import com.vkr.matchmaking_service.redis.service.lobby.LobbyService;
import com.vkr.matchmaking_service.redis.service.series.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/series")
@RequiredArgsConstructor
public class SeriesController {

    private final LobbyService lobbyService;
    private final SeriesService seriesService;

    @GetMapping("/{seriesId}")
    public SeriesCache getSeries(@PathVariable String seriesId) {
        return seriesService.getSeriesCache(UUID.fromString(seriesId));
    }

    @GetMapping("/{seriesId}/matches")
    public List<MatchCache> getSeriesMatches(@PathVariable String seriesId) {
        return seriesService.getMatchCachesBySeries(UUID.fromString(seriesId));
    }

    @GetMapping("/{seriesId}/matches/{matchId}")
    public MatchCache getSeriesMatch(@PathVariable String seriesId, @PathVariable String matchId) {
        return seriesService.getMatchCache(UUID.fromString(seriesId), UUID.fromString(matchId));
    }
}
