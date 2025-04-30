package com.vkr.matchmaking_service.redis.service.series;

import com.vkr.matchmaking_service.redis.cache.lobby.Lobby;
import com.vkr.matchmaking_service.redis.cache.match.MatchCache;
import com.vkr.matchmaking_service.redis.cache.series.SeriesCache;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SeriesService {

    SeriesCache getSeriesCache(UUID tournamentMatchId);

    List<SeriesCache> getAll();

    List<SeriesCache> getAllByTournamentId(UUID tournamentId);

    Map<Integer, MatchCache> getMatchCachesBySeries(UUID tournamentMatchId);

    MatchCache getMatchCache(UUID tournamentMatchId, int orderSeries);

    void initSeriesCache(Lobby lobby);

    MatchCache initNextMatchCache(Lobby lobby);

    void deleteAllSeries();

    void deleteAllMatches(UUID seriesId);
}
