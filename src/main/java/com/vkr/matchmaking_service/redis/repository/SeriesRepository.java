package com.vkr.matchmaking_service.redis.repository;

import com.vkr.matchmaking_service.redis.cache.series.SeriesCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeriesRepository extends KeyValueRepository<SeriesCache, UUID> {
    SeriesCache findByTournamentMatchId(UUID tournamentMatchId);

    List<SeriesCache> findAllByTournamentId(UUID tournamentId);
}
