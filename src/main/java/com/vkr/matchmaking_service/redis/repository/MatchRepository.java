package com.vkr.matchmaking_service.redis.repository;

import com.vkr.matchmaking_service.redis.cache.match.MatchCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatchRepository extends KeyValueRepository<MatchCache, UUID> {
    List<MatchCache> findAllByTournamentMatchId(UUID tournamentMatchId);

    MatchCache findByIdAndTournamentMatchId(UUID id, UUID tournamentMatchId);
}
