package com.vkr.matchmaking_service.redis.repository;

import com.vkr.matchmaking_service.redis.cache.match.MatchCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MatchRepository extends KeyValueRepository<MatchCache, UUID> {

    MatchCache findByTournamentMatchIdAndSeriesOrder(UUID tournamentMatchId, int seriesOrder);

    void deleteAllByTournamentMatchId(UUID tournamentMatchId);
}
