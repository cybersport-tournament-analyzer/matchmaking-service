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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Map<Integer, MatchCache> getMatchCachesBySeries(UUID tournamentMatchId) {
        return seriesRepository.findByTournamentMatchId(tournamentMatchId).getMatches();
    }

    @Override
    public MatchCache getMatchCache(UUID tournamentMatchId, int seriesOrder) {
        return matchRepository.findByTournamentMatchIdAndSeriesOrder(tournamentMatchId, seriesOrder);
    }

    @Override
    public void initSeriesCache(Lobby lobby) {

        Map<Integer, SeriesCache.Kda> team1Kda = new HashMap<>();
        Map<Integer, SeriesCache.Kda> team2Kda = new HashMap<>();


        for(int i = 1; i <= lobby.maxPlayersPerTeam() * 2; ++i) {
            if(i <= lobby.maxPlayersPerTeam()) {
                team1Kda.put(i, new SeriesCache.Kda(0,0,0));
            } else {
                team2Kda.put(i, new SeriesCache.Kda(0,0,0));
            }
        }

        Map<Integer, MatchCache> matches = new HashMap<>();


        SeriesCache seriesCache = SeriesCache.builder()
                .tournamentMatchId(lobby.getId())
                .matches(matches)
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

    @Override
    public MatchCache initNextMatchCache(Lobby lobby) {
        int mapNumber = lobby.getCurrentMapNumber();

        Map<Integer, MatchCache.Kda> team1Kda1 = new HashMap<>();
        Map<Integer, MatchCache.Kda> team2Kda2 = new HashMap<>();

        for(int i = 1; i <= lobby.maxPlayersPerTeam() * 2; ++i) {
            if(i <= lobby.maxPlayersPerTeam()) {
                team1Kda1.put(i, new MatchCache.Kda(0,0,0));
            } else {
                team2Kda2.put(i, new MatchCache.Kda(0,0,0));
            }
        }

        String matchId = lobby.getId() + ":" + lobby.getCurrentMapNumber();

        MatchCache matchCache = MatchCache.builder()
                .id(matchId)
                .tournamentMatchId(lobby.getId())
                .tournamentId(lobby.getTournamentId())
                .match(null)
                .mode(lobby.getMode())
                .startTime(LocalDateTime.now())
                .endTime(null)
                .duration(null)
                .format(lobby.getFormat())
                .seriesOrder(mapNumber)
                .team1(lobby.getTeam1())
                .team2(lobby.getTeam2())
                .team1Name(lobby.getTeam1Name())
                .team1Score(0)
                .team2Name(lobby.getTeam2Name())
                .team2Score(0)
                .team1Kda(team1Kda1)
                .team2Kda(team2Kda2)
                .build();

        matchRepository.save(matchCache);

       return matchCache;
    }

    @Override
    public void deleteAllSeries() {
        seriesRepository.deleteAll();
    }

    @Override
    public void deleteAllMatches(UUID seriesId) {
        matchRepository.deleteAllByTournamentMatchId(seriesId);
    }
}
