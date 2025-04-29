package com.vkr.matchmaking_service.redis.service.series;

import com.vkr.matchmaking_service.redis.cache.lobby.Lobby;
import com.vkr.matchmaking_service.redis.cache.match.MatchCache;
import com.vkr.matchmaking_service.redis.cache.series.SeriesCache;
import com.vkr.matchmaking_service.redis.repository.MatchRepository;
import com.vkr.matchmaking_service.redis.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

    @Override
    public void initSeriesCache(Lobby lobby) {

        Map<Integer, MatchCache.Kda> team1Kda1 = new HashMap<>();
        Map<Integer, MatchCache.Kda> team2Kda2 = new HashMap<>();

        for(int i = 1; i <= lobby.maxPlayersPerTeam() * 2; ++i) {
            if(i <= lobby.maxPlayersPerTeam()) {
                System.out.println(i);
                team1Kda1.put(i, new MatchCache.Kda(0,0,0));
            } else {
                System.out.println(i);
                team2Kda2.put(i, new MatchCache.Kda(0,0,0));
            }
        }

        MatchCache firstMatch = MatchCache.builder()
                .id(UUID.randomUUID())
                .tournamentMatchId(lobby.getId())
                .tournamentId(lobby.getTournamentId())
                .match(null)
                .mode(lobby.getMode())
                .startTime(null)
                .endTime(null)
                .duration(null)
                .format(lobby.getFormat())
                .seriesOrder(0)
                .team1(lobby.getTeam1())
                .team2(lobby.getTeam2())
                .team1Name(lobby.getTeam1Name())
                .team1Score(lobby.getTeam1Score())
                .team2Name(lobby.getTeam2Name())
                .team2Score(lobby.getTeam2Score())
                .team1Kda(team1Kda1)
                .team2Kda(team2Kda2)
                .build();

        matchRepository.save(firstMatch);

        Map<Integer, SeriesCache.Kda> team1Kda = new HashMap<>();
        Map<Integer, SeriesCache.Kda> team2Kda = new HashMap<>();

        for(int i = 1; i <= lobby.maxPlayersPerTeam() * 2; ++i) {
            if(i <= lobby.maxPlayersPerTeam()) {
                System.out.println(i);
                team1Kda.put(i, new SeriesCache.Kda(0,0,0));
            } else {
                System.out.println(i);
                team2Kda.put(i, new SeriesCache.Kda(0,0,0));
            }
        }

        SeriesCache seriesCache = SeriesCache.builder()
                .tournamentMatchId(lobby.getId())
                .matches(List.of(firstMatch))
                .dateTime(LocalDateTime.now())
                .status("Waiting for start")
                .pickBanSession(null)
                .tournamentId(lobby.getTournamentId())
                .team1(lobby.getTeam1())
                .team2(lobby.getTeam2())
                .team1Name(lobby.getTeam1Name())
                .team1Score(lobby.getTeam1Score())
                .team2Name(lobby.getTeam2Name())
                .team2Score(lobby.getTeam2Score())
                .team1Kda(team1Kda)
                .team2Kda(team2Kda)
                .build();

        seriesRepository.save(seriesCache);
    }
}
