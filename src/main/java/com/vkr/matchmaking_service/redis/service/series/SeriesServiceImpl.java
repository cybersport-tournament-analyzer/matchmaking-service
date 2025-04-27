package com.vkr.matchmaking_service.redis.service.series;

import com.vkr.matchmaking_service.redis.cache.match.MatchCache;
import com.vkr.matchmaking_service.redis.cache.series.SeriesCache;
import com.vkr.matchmaking_service.redis.repository.MatchRepository;
import com.vkr.matchmaking_service.redis.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeriesServiceImpl implements SeriesService {

    private final SeriesRepository seriesRepository;
    private final MatchRepository matchRepository;

    @Override
    public SeriesCache getSeriesCache(UUID tournamentMatchId) {
        return seriesRepository.findByTournamentMatchId(tournamentMatchId);
    }

    @Override
    public List<SeriesCache> getAll() {
        return seriesRepository.findAll();
    }

    @Override
    public List<SeriesCache> getAllByTournamentId(UUID tournamentId) {
        return seriesRepository.findAllByTournamentId(tournamentId);
    }

    @Override
    public List<MatchCache> getMatchCachesBySeries(UUID tournamentMatchId) {
        return matchRepository.findAllByTournamentMatchId(tournamentMatchId);
    }

    @Override
    public MatchCache getMatchCache(UUID tournamentMatchId, UUID matchId) {
        return matchRepository.findByIdAndTournamentMatchId(matchId, tournamentMatchId);
    }
}
