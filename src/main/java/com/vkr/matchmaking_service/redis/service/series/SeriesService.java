package com.vkr.matchmaking_service.redis.service.series;

import com.vkr.matchmaking_service.redis.cache.match.MatchCache;
import com.vkr.matchmaking_service.redis.cache.series.SeriesCache;

import java.util.List;
import java.util.UUID;

public interface SeriesService {

    SeriesCache getSeriesCache(UUID tournamentMatchId);

    List<SeriesCache> getAll();

    List<SeriesCache> getAllByTournamentId(UUID tournamentId);

    List<MatchCache> getMatchCachesBySeries(UUID tournamentMatchId);
    MatchCache getMatchCache(UUID tournamentMatchId, UUID matchId);
}
