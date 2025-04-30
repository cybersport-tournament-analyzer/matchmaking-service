package com.vkr.matchmaking_service.service.webhooks;

import com.vkr.matchmaking_service.dto.lobby.CreateMatchDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.exception.MatchNotFoundException;
import com.vkr.matchmaking_service.kafka.event.matchEnd.MatchEndEvent;
import com.vkr.matchmaking_service.kafka.event.matchStart.MatchStartEvent;
import com.vkr.matchmaking_service.kafka.producer.match.MatchEndProducer;
import com.vkr.matchmaking_service.kafka.producer.match.MatchStartProducer;
import com.vkr.matchmaking_service.kafka.producer.round.RoundEndProducer;
import com.vkr.matchmaking_service.redis.cache.lobby.Lobby;
import com.vkr.matchmaking_service.redis.cache.match.MatchCache;
import com.vkr.matchmaking_service.redis.cache.series.SeriesCache;
import com.vkr.matchmaking_service.redis.repository.MatchRepository;
import com.vkr.matchmaking_service.redis.repository.SeriesRepository;
import com.vkr.matchmaking_service.redis.service.lobby.LobbyService;
import com.vkr.matchmaking_service.redis.service.series.SeriesService;
import com.vkr.matchmaking_service.service.server.ServerService;
import com.vkr.matchmaking_service.utils.ConsoleLogParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhooksServiceImpl implements WebhooksService {

    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyService lobbyService;
    private final ServerService serverService;


    private final MatchEndProducer matchEndProducer;
    private final MatchStartProducer matchStartProducer;
    private final RoundEndProducer roundEndProducer;
    private final ConsoleLogParser consoleLogParser;

    private final SeriesRepository seriesRepository;
    private final MatchRepository matchRepository;
    private final SeriesService seriesService;

    @Override
    public void handleEvent(Match match, String lobbyId) throws IOException, InterruptedException {
        log.info("handleEvent: " + match);
        Lobby currentLobby = lobbyService.getLobbyById(lobbyId);
        if (match.getEvents().get(match.getEvents().size() - 1).getEvent().equals("match_started")) {
            matchStartProducer.produce(new MatchStartEvent(currentLobby.getId(), OffsetDateTime.now()));
        }
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, matchToDto(match, lobbyId));
    }

    @Override
    public void handleMatchEnd(Match match, String lobbyId) throws IOException, InterruptedException {
        log.info("match end: " + match);
        updateEndedMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, matchToDto(match, lobbyId));
    }

    @Override
    public void handleRoundEnd(Match match, String lobbyId) throws IOException, InterruptedException {
        log.info("round end: " + match);
        List<String> consoleLogs = serverService.getConsoleLogs(match.getGame_server_id(), 350);
        roundEndProducer.produce(consoleLogParser.parseRoundEnd(consoleLogs, match, lobbyService.getLobbyById(lobbyId).getId(), lobbyService.getLobbyById(lobbyId).getTournamentId()));
        updateMatch(match, lobbyId);
        messagingTemplate.convertAndSend("/topic/match/" + lobbyId, matchToDto(match, lobbyId));
    }

    @Override
    public Match getMatchById(String lobbyId) {
        Lobby currentLobby = lobbyService.getLobbyById(lobbyId);
        return currentLobby.getMatches().get(currentLobby.getMatches().size() - 1);
    }

    private void deleteFileFromServer(Lobby lobby, String serverId) throws IOException, InterruptedException {
        if (lobby.getMode().equals("1vs1"))
            serverService.deleteFileFromServer(serverId, "cfg/live_server.cfg");
    }

    private void checkMissingPlayers(Match match) {
        if (match.getCancel_reason() != null && match.getCancel_reason().startsWith("MISSING_PLAYERS")) {
            Set<String> missingPlayers = new HashSet<>(Arrays.asList(match.getCancel_reason()
                    .split(":")[1].split(",")));

            boolean allMissingFromTeam1 = match.getPlayers().stream()
                    .filter(p -> "team1".equals(p.getTeam()))
                    .allMatch(p -> missingPlayers.contains(p.getSteam_id_64()));

            boolean allMissingFromTeam2 = match.getPlayers().stream()
                    .filter(p -> "team2".equals(p.getTeam()))
                    .allMatch(p -> missingPlayers.contains(p.getSteam_id_64()));

            if (allMissingFromTeam1 && allMissingFromTeam2) {
                match.getTeam1().getStats().setScore(0);
                match.getTeam2().getStats().setScore(0);
            } else if (allMissingFromTeam1) {
                match.getTeam1().getStats().setScore(0);
                match.getTeam2().getStats().setScore(13);
            } else if (allMissingFromTeam2) {
                match.getTeam2().getStats().setScore(0);
                match.getTeam1().getStats().setScore(13);
            }

        }
    }

    private void updateMatch(Match match, String lobbyId) throws IOException, InterruptedException {
        Lobby currentLobby = lobbyService.getLobbyById(lobbyId);
        Match currentMatch = currentLobby.getMatches().stream().
                filter(match1 -> match1.getId().equals(match.getId())).findFirst().orElseThrow(
                        () -> new MatchNotFoundException("Match not found in lobby: " + lobbyId)
                );
        currentLobby.getMatches().remove(currentMatch);
        currentLobby.getMatches().add(match);
        if (match.getEvents().get(match.getEvents().size() - 1).getEvent().equals("server_ready_for_players")) {
            currentLobby.setLink("steam://rungameid/730//+" + serverService.getServerIp(match.getGame_server_id()));
            messagingTemplate.convertAndSend("/topic/lobby/" + lobbyId, currentLobby);
        }
        lobbyService.save(currentLobby);
    }

    private void updateEndedMatch(Match match, String lobbyId) throws IOException, InterruptedException {
        Lobby currentLobby = lobbyService.getLobbyById(lobbyId);
        Match currentMatch = currentLobby.getMatches().stream().
                filter(match1 -> match1.getId().equals(match.getId())).findFirst().orElseThrow(
                        () -> new MatchNotFoundException("Match not found in lobby: " + lobbyId)
                );
        currentLobby.getMatches().remove(currentMatch);
        currentLobby.getMatches().add(match);

        MatchCache matchCache = seriesRepository.findByTournamentMatchId(currentLobby.getId())
                .getMatches().get(currentLobby.getCurrentMapNumber());

        matchCache.setEndTime(LocalDateTime.now());
        matchCache.setDuration(Duration.between(matchCache.getStartTime(), matchCache.getEndTime()));
        matchCache.setMatch(match);

        if (matchCache.getTeam1Name().equals(currentLobby.getTeam1Name())) {
            matchCache.setTeam1Score(match.getTeam1().getStats().getScore());
            matchCache.setTeam2Score(match.getTeam2().getStats().getScore());
        } else {
            matchCache.setTeam1Name(currentLobby.getTeam2Name());
            matchCache.setTeam2Name(currentLobby.getTeam1Name());
            matchCache.setTeam2Score(match.getTeam1().getStats().getScore());
            matchCache.setTeam1Score(match.getTeam2().getStats().getScore());
        }

        AtomicInteger c = new AtomicInteger(1);

        match.getPlayers().forEach(p -> {
            if (!p.getTeam().equals("spectator")) {
                MatchCache.Kda kda = new MatchCache.Kda(
                        p.getStats().getKills(),
                        p.getStats().getDeaths(),
                        p.getStats().getAssists()
                );
                if (c.get() <= currentLobby.maxPlayersPerTeam()) {
                    matchCache.getTeam1Kda().put(c.getAndIncrement(), kda);
                } else {
                    matchCache.getTeam2Kda().put(c.getAndIncrement(), kda);
                }
            }
        });

        matchRepository.save(matchCache);

        SeriesCache seriesCache = seriesRepository.findByTournamentMatchId(currentLobby.getId());
        seriesCache.getMatches().put(currentLobby.getCurrentMapNumber(), matchCache);

        checkMissingPlayers(match);

        if (match.getTeam1().getStats().getScore() > match.getTeam2().getStats().getScore()) {
            if (match.getTeam1().getName().equals(currentLobby.getTeam1Name())) {
                seriesCache.setTeam1Score(seriesCache.getTeam1Score() + 1);
                currentLobby.setTeam1Score(currentLobby.getTeam1Score() + 1);
            } else {
                seriesCache.setTeam2Score(seriesCache.getTeam2Score() + 1);
                currentLobby.setTeam2Score(currentLobby.getTeam2Score() + 1);
            }
        } else if (match.getTeam1().getStats().getScore() < match.getTeam2().getStats().getScore()) {
            if (match.getTeam2().getName().equals(currentLobby.getTeam2Name())) {
                seriesCache.setTeam2Score(seriesCache.getTeam2Score() + 1);
                currentLobby.setTeam2Score(currentLobby.getTeam2Score() + 1);
            } else {
                seriesCache.setTeam1Score(seriesCache.getTeam1Score() + 1);
                currentLobby.setTeam1Score(currentLobby.getTeam1Score() + 1);
            }
        }


        AtomicInteger c2 = new AtomicInteger(1);

        match.getPlayers().forEach(p -> {
            if (!p.getTeam().equals("spectator")) {
                SeriesCache.Kda kda = new SeriesCache.Kda(
                        p.getStats().getKills(),
                        p.getStats().getDeaths(),
                        p.getStats().getAssists()
                );
                if (c2.get() <= currentLobby.maxPlayersPerTeam()) {
                    if (currentLobby.getCurrentMapNumber() == 0) {
                        seriesCache.getTeam1Kda().put(c2.getAndIncrement(), kda);
                    } else {
                        kda.setKills(seriesCache.getTeam1Kda().get(c2.get()).getKills() + p.getStats().getKills());
                        kda.setAssists(seriesCache.getTeam1Kda().get(c2.get()).getAssists() + p.getStats().getAssists());
                        kda.setDeaths(seriesCache.getTeam1Kda().get(c2.get()).getDeaths() + p.getStats().getDeaths());
                        seriesCache.getTeam1Kda().put(c2.getAndIncrement(), kda);
                    }
                } else {
                    if (currentLobby.getCurrentMapNumber() == 0) {
                        seriesCache.getTeam2Kda().put(c2.getAndIncrement(), kda);
                    } else {
                        kda.setKills(seriesCache.getTeam2Kda().get(c2.get()).getKills() + p.getStats().getKills());
                        kda.setAssists(seriesCache.getTeam2Kda().get(c2.get()).getAssists() + p.getStats().getAssists());
                        kda.setDeaths(seriesCache.getTeam2Kda().get(c2.get()).getDeaths() + p.getStats().getDeaths());
                        seriesCache.getTeam2Kda().put(c2.getAndIncrement(), kda);
                    }
                }
            }
        });

        switch (currentLobby.getFormat()) {
            case "bo1" -> {
                String serverId = getMatchById(lobbyId).getGame_server_id();
                serverService.stopServer(serverId);
                deleteFileFromServer(currentLobby, serverId);
                seriesCache.setStatus("Concluded");
                seriesRepository.save(seriesCache);
                matchEndProducer.produce(new MatchEndEvent(currentLobby.getId(),
                        currentLobby.getTournamentId(),
                        currentLobby.getTeam1Score(),
                        currentLobby.getTeam2Score(),
                        OffsetDateTime.now(),
                        currentLobby.getMatches().get(0)));
                lobbyService.deleteLobby(UUID.fromString(lobbyId));
            }
            case "bo3" -> {
                String serverId = getMatchById(lobbyId).getGame_server_id();
                currentLobby.setCurrentMapNumber(currentLobby.getCurrentMapNumber() + 1);
                matchEndProducer.produce(new MatchEndEvent(currentLobby.getId(), currentLobby.getTournamentId(), currentLobby.getTeam1Score(), currentLobby.getTeam2Score(), OffsetDateTime.now(), currentLobby.getMatches().get(currentLobby.getMatches().size() - 1)));
                if (currentLobby.getTeam1Score() == 2 || currentLobby.getTeam2Score() == 2) {
                    serverService.stopServer(serverId);
                    deleteFileFromServer(currentLobby, serverId);
                    seriesCache.setStatus("Concluded");
                    seriesRepository.save(seriesCache);
                    lobbyService.deleteLobby(UUID.fromString(lobbyId));
                } else {
                    lobbyService.startMatch(currentLobby);
                }
            }
            case "bo5" -> {
                String serverId = getMatchById(lobbyId).getGame_server_id();
                currentLobby.setCurrentMapNumber(currentLobby.getCurrentMapNumber() + 1);
                matchEndProducer.produce(new MatchEndEvent(currentLobby.getId(), currentLobby.getTournamentId(), currentLobby.getTeam1Score(), currentLobby.getTeam2Score(), OffsetDateTime.now(), currentLobby.getMatches().get(currentLobby.getMatches().size() - 1)));
                if (currentLobby.getTeam1Score() == 3 || currentLobby.getTeam2Score() == 3) {
                    serverService.stopServer(serverId);
                    deleteFileFromServer(currentLobby, serverId);
                    seriesCache.setStatus("Concluded");
                    seriesRepository.save(seriesCache);
                    lobbyService.deleteLobby(UUID.fromString(lobbyId));
                } else {
                    lobbyService.startMatch(currentLobby);
                }
            }
        }
        lobbyService.save(currentLobby);
    }

    private CreateMatchDto matchToDto(Match match, String lobbyId) {
        Lobby currentLobby = lobbyService.getLobbyById(lobbyId);
        CreateMatchDto createMatchDto = new CreateMatchDto();
        createMatchDto.setMatch(match);
        createMatchDto.setFormat(currentLobby.getFormat());
        createMatchDto.setMode(currentLobby.getMode());
        createMatchDto.setTeam1Score(currentLobby.getTeam1Score());
        createMatchDto.setTeam2Score(currentLobby.getTeam2Score());
        createMatchDto.setTeam1Name(currentLobby.getTeam1Name());
        createMatchDto.setTeam2Name(currentLobby.getTeam2Name());
        return createMatchDto;
    }
}
