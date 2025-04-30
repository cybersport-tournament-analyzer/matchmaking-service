package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.redis.cache.match.MatchCache;
import com.vkr.matchmaking_service.redis.cache.series.SeriesCache;
import com.vkr.matchmaking_service.redis.service.lobby.LobbyService;
import com.vkr.matchmaking_service.redis.service.series.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    public Map<Integer, MatchCache> getSeriesMatches(@PathVariable String seriesId) {
        return seriesService.getMatchCachesBySeries(UUID.fromString(seriesId));
    }

    @GetMapping("/{seriesId}/matches/{orderSeries}")
    public MatchCache getSeriesMatch(@PathVariable String seriesId, @PathVariable int orderSeries) {
        return seriesService.getMatchCache(UUID.fromString(seriesId), orderSeries);
    }

    @DeleteMapping("/delete")
    public void deleteSeries() {
        seriesService.deleteAllSeries();
    }

    @DeleteMapping("/delete/{seriesId}")
    public void deleteSeriesMatches(@PathVariable String seriesId) {
        seriesService.deleteAllMatches(UUID.fromString(seriesId));
    }

    @GetMapping
    public List<SeriesCache> getAllSeries() {
        return seriesService.getAll();
    }

    @GetMapping("/tournament/{tournamentId}")
    public List<SeriesCache> getAllTournament(@PathVariable String tournamentId) {
        return seriesService.getAllByTournamentId(UUID.fromString(tournamentId));
    }
}
