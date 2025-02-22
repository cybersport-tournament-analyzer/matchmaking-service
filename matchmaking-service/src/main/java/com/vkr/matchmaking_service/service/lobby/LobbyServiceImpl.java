package com.vkr.matchmaking_service.service.lobby;

import com.vkr.matchmaking_service.client.UserServiceClient;
import com.vkr.matchmaking_service.dto.match.MatchSettingsDto;
import com.vkr.matchmaking_service.dto.match.MatchStartingDto;
import com.vkr.matchmaking_service.dto.match.StartMatchPlayerDto;
import com.vkr.matchmaking_service.dto.user.UserDto;
import com.vkr.matchmaking_service.entity.lobby.Lobby;
import com.vkr.matchmaking_service.entity.pickbans.Action;
import com.vkr.matchmaking_service.entity.pickbans.PickBanAction;
import com.vkr.matchmaking_service.entity.pickbans.PickBanSession;
import com.vkr.matchmaking_service.entity.pickbans.SideSelection;
import com.vkr.matchmaking_service.exception.LobbyIsFullException;
import com.vkr.matchmaking_service.exception.LobbyNotFoundException;
import com.vkr.matchmaking_service.exception.TeamIsFullException;
import com.vkr.matchmaking_service.exception.WrongInputException;
import com.vkr.matchmaking_service.service.server.ServerService;
import com.vkr.matchmaking_service.utils.CustomTimerTask;
import com.vkr.matchmaking_service.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LobbyServiceImpl implements LobbyService {

    private final UserServiceClient userServiceClient;
    private final JedisPool jedisPool;
    private final ServerService serverService;
    private static final String LOBBY_KEY_PREFIX = "lobby:";

    @Override
    public List<Lobby> getAllLobbies() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys(LOBBY_KEY_PREFIX + "*");
            if (keys == null || keys.isEmpty()) return Collections.emptyList();

            List<Lobby> lobbies = new ArrayList<>();
            for (String key : keys) {
                String json = jedis.get(key);
                if (json != null) {
                    lobbies.add(JsonUtils.fromJson(json, Lobby.class));
                }
            }
            return lobbies;
        }
    }


    @Override
    public Lobby createLobby(String mode, String format, String steamId) {

        if (!List.of("1x1", "2x2", "5x5").contains(mode)) {
            throw new WrongInputException("Wrong game mode!");
        }

        UserDto creator = userServiceClient.getUserBySteamId(steamId);
        Lobby lobby = new Lobby(UUID.randomUUID(), mode, new PickBanSession(), format, new ArrayList<>(), new ArrayList<>());
        lobby.getTeam1().add(creator);
        lobby.getTeam1().get(0).setCaptain(true);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(LOBBY_KEY_PREFIX + lobby.getId(), 600, JsonUtils.toJson(lobby));
        }

        return lobby;
    }

    @Override
    public void addPlayer(UUID lobbyId, String steamId, String team) {
        String key = LOBBY_KEY_PREFIX + lobbyId;

        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            if (json == null) throw new LobbyNotFoundException("Lobby not found!");

            Lobby lobby = JsonUtils.fromJson(json, Lobby.class);
            if (lobby.full()) throw new LobbyIsFullException("Lobby is full!");

            List<UserDto> targetTeam = "team1".equals(team) ? lobby.getTeam1() : lobby.getTeam2();
            if (targetTeam.size() >= lobby.maxPlayersPerTeam()) {
                throw new TeamIsFullException("Chosen team is full!");
            }

            UserDto currentPlayer = userServiceClient.getUserBySteamId(steamId);
            if (targetTeam.isEmpty()) {
                targetTeam.add(currentPlayer);
                targetTeam.get(0).setCaptain(true);
            } else {
                targetTeam.add(currentPlayer);
            }

            Transaction transaction = jedis.multi();
            transaction.setex(key, 600, JsonUtils.toJson(lobby));
            transaction.exec();
        }
    }

    @Override
    public void removePlayer(UUID lobbyId, String steamId) {
        String key = LOBBY_KEY_PREFIX + lobbyId;

        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            if (json == null) throw new LobbyNotFoundException("Lobby not found!");

            Lobby lobby = JsonUtils.fromJson(json, Lobby.class);
            lobby.getTeam1().removeIf(player -> player.getSteamId().equals(steamId));
            lobby.getTeam2().removeIf(player -> player.getSteamId().equals(steamId));

            if (lobby.getTeam1().isEmpty() && lobby.getTeam2().isEmpty()) {
                jedis.del(key);
            } else {
                jedis.setex(key, 600, JsonUtils.toJson(lobby));
            }
        }
    }

    @Override
    public Lobby getLobbyById(String lobbyId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(LOBBY_KEY_PREFIX + lobbyId);
            if (json == null) throw new LobbyNotFoundException("Lobby not found!");
            return JsonUtils.fromJson(json, Lobby.class);
        }
    }

    @Override
    public void setReady(UUID lobbyId, String steamId, boolean ready) {
        String key = LOBBY_KEY_PREFIX + lobbyId;

        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            if (json == null) throw new LobbyNotFoundException("Lobby not found!");

            Lobby lobby = JsonUtils.fromJson(json, Lobby.class);
            lobby.setReady(steamId, ready);

            jedis.setex(key, 600, JsonUtils.toJson(lobby));
        }
    }

    @Override
    public boolean checkAndStartPickBan(String lobbyId) {
        Lobby lobby = getLobbyById(lobbyId);
        if (lobby == null) return false;

        if (lobby.getTeam1().stream().allMatch(UserDto::isReady) &&
                lobby.getTeam2().stream().allMatch(UserDto::isReady) &&
                lobby.getTeam1().size() + lobby.getTeam2().size() == lobby.maxPlayersPerTeam() * 2) {

            initializePickBanSession(lobby);
            return true;
        }

        return false;
    }

    @Override
    public void processPickBanAction(UUID lobbyId, String steamId, Action actionType, String map, String side) {
        String key = LOBBY_KEY_PREFIX + lobbyId;

        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            Lobby lobby = JsonUtils.fromJson(json, Lobby.class);
            PickBanSession session = lobby.getPickBanSession();


//            validateAction(session, steamId);

            PickBanAction action = PickBanAction.builder().team(getTeamForCaptain(steamId, lobby))
                    .action(actionType).mapOrSide(map != null ? map : side).build();

            updateSessionState(session, action);
            lobby.setPickBanSession(session);

            jedis.setex(key, 6000, JsonUtils.toJson(lobby));
            startNewTimerIfNeeded(session);
        }
    }

    @Override
    public void handleTimeout(UUID lobbyId) {
        Lobby lobby = getLobbyById(lobbyId.toString());
        PickBanSession session = lobby.getPickBanSession();

        String randomMap = session.getMaps().get((int) (Math.random() * session.getMaps().size()));
        String team = session.getCurrentTeamTurn();

        PickBanAction action = PickBanAction.builder().team(team)
                .action(session.getNextActionType()).mapOrSide(randomMap).build();

        session.getActionsLogs().add(action);
        updateSessionState(session, action);
        lobby.setPickBanSession(session);
        saveAndNotify(lobby);
    }

    @Override
    public void initializePickBanSession(Lobby lobby) {
//        String firstTeam = (new Random().nextInt(2) == 0) ? "team1" : "team2";
        String firstTeam = "team1";

        PickBanSession session = lobby.getPickBanSession();
        session.setLobbyId(lobby.getId());
        session.setFormat(lobby.getFormat());

        session.setCurrentTeamTurn(firstTeam);
        session.setNextActionType(Action.BAN);

        startNewTimerIfNeeded(session);
        lobby.setPickBanSession(session);
        saveAndNotify(lobby);
    }

    @Override
    public void startMatch(Lobby lobby) throws IOException, InterruptedException {
        String serverId = serverService.getAvailableServer().getId();
        serverService.startServer(serverId);

        MatchStartingDto matchStartingDto = new MatchStartingDto();
        matchStartingDto.setGame_server_id(serverId);

        MatchSettingsDto matchSettingsDto = new MatchSettingsDto();
        matchSettingsDto.setMap(lobby.getPickBanSession().getPickedMaps().get(0));
        matchSettingsDto.setTeam_size(lobby.maxPlayersPerTeam());
        matchSettingsDto.setPassword("");

        List<StartMatchPlayerDto> players = new ArrayList<>();
        for (UserDto user : lobby.getTeam1()) {
            StartMatchPlayerDto playerDto = new StartMatchPlayerDto();
            playerDto.setTeam("team1");
            playerDto.setSteam_id_64(user.getSteamId());
            players.add(playerDto);
        }
        for (UserDto user : lobby.getTeam2()) {
            StartMatchPlayerDto playerDto = new StartMatchPlayerDto();
            playerDto.setTeam("team2");
            playerDto.setSteam_id_64(user.getSteamId());
            players.add(playerDto);
        }

        matchStartingDto.setPlayers(players);
        matchStartingDto.setSettings(matchSettingsDto);

        serverService.startMatch(matchStartingDto);
    }

    private void updateSessionState(PickBanSession session, PickBanAction action) {
        switch (session.getFormat()) {
            case "bo1":
                updateBo1State(session, action);
                break;
            case "bo3":
                updateBo3State(session, action);
                break;
            case "bo5":
                updateBo5State(session, action);
                break;
        }
    }

    private void updateBo1State(PickBanSession session, PickBanAction action) {
        if (Action.BAN.equals(action.getAction()) && session.getCurrentTeamTurn().equals(action.getTeam())) {
            session.getMaps().remove(action.getMapOrSide());
            session.getActionsLogs().add(action);
            session.setCurrentTeamTurn(getOppositeTeam(action.getTeam()));
        }

        if (session.getActionsLogs().size() == 6 && session.getNextActionType().equals(Action.PICK_SIDE)
        && session.getCurrentTeamTurn().equals(action.getTeam())) {
            String lastMap = session.getMaps().remove(0);
            session.getPickedMaps().add(lastMap);
            session.getSideSelections().add(new SideSelection(action.getMapOrSide(), action.getTeam()));
            session.setCompleted(true);
            session.setNextActionType(null);
            session.setCurrentTeamTurn(null);
            session.getActionsLogs().add(action);
        }

        if (session.getMaps().size() == 1) {
            session.setNextActionType(Action.PICK_SIDE);
            session.setCurrentTeamTurn(getOppositeTeam(action.getTeam()));
        }

    }

    private void updateBo3State(PickBanSession session, PickBanAction action) {
        List<String> maps = session.getMaps();
        List<PickBanAction> actions = session.getActionsLogs();

        if (session.getNextActionType().equals(action.getAction()) && action.getAction().equals(Action.BAN)
                && maps.size() != 1 &&
                session.getCurrentTeamTurn().equals(action.getTeam())) {
            maps.remove(action.getMapOrSide());
            session.getActionsLogs().add(action);
            session.setCurrentTeamTurn(getOppositeTeam(action.getTeam()));
        } else if (session.getNextActionType().equals(action.getAction())
                && action.getAction().equals(Action.PICK)  &&
                session.getCurrentTeamTurn().equals(action.getTeam())) {
            session.getPickedMaps().add(action.getMapOrSide());
            session.getActionsLogs().add(action);
            session.setCurrentTeamTurn(getOppositeTeam(action.getTeam()));
        } else if (session.getNextActionType().equals(action.getAction()) &&
                action.getAction().equals(Action.PICK_SIDE) &&
                session.getCurrentTeamTurn().equals(action.getTeam())) {
            session.getSideSelections().add(new SideSelection(action.getMapOrSide(), action.getTeam()));
            session.getMaps().remove(session.getActionsLogs().get(session.getActionsLogs().size() - 1).getMapOrSide());
            session.getActionsLogs().add(action);
        }

        if (actions.size() < 2) {
            session.setNextActionType(Action.BAN);
        } else if (actions.size() < 4) {
            if (actions.size() % 2 == 0) {
                session.setNextActionType(Action.PICK);
            } else {
                session.setNextActionType(Action.PICK_SIDE);
            }
        } else if (actions.size() < 6) {
            if (actions.size() % 2 == 0) {
                session.setNextActionType(Action.PICK);
            } else {
                session.setNextActionType(Action.PICK_SIDE);
            }
        } else if (actions.size() < 8) {
            session.setNextActionType(Action.BAN);
        }


        if (maps.size() == 1) {
            String lastMap = maps.remove(0);
            session.getPickedMaps().add(lastMap);
            session.getSideSelections().add(new SideSelection(getRandomSide(), action.getTeam()));
            session.getActionsLogs().add(new PickBanAction("LEFT OVER", Action.PICK, lastMap));
            session.getActionsLogs().add(new PickBanAction("RANDOM SIDE", Action.PICK_SIDE,
                    session.getSideSelections().get(session.getSideSelections().size() - 1).getSide()));
            session.setCompleted(true);
            session.setNextActionType(null);
        }
    }

    private void updateBo5State(PickBanSession session, PickBanAction action) {
        List<String> maps = session.getMaps();
        List<PickBanAction> actions = session.getActionsLogs();

        if (session.getNextActionType().equals(action.getAction()) && action.getAction().equals(Action.BAN)
                && maps.size() != 1 &&
                session.getCurrentTeamTurn().equals(action.getTeam())) {
            maps.remove(action.getMapOrSide());
            session.getActionsLogs().add(action);
            session.setCurrentTeamTurn(getOppositeTeam(action.getTeam()));
        } else if (session.getNextActionType().equals(action.getAction())
                && action.getAction().equals(Action.PICK)  &&
                session.getCurrentTeamTurn().equals(action.getTeam())) {
            session.getPickedMaps().add(action.getMapOrSide());
            session.getActionsLogs().add(action);
            session.setCurrentTeamTurn(getOppositeTeam(action.getTeam()));
        } else if (session.getNextActionType().equals(action.getAction()) &&
                action.getAction().equals(Action.PICK_SIDE) &&
                session.getCurrentTeamTurn().equals(action.getTeam())) {
            session.getSideSelections().add(new SideSelection(action.getMapOrSide(), action.getTeam()));
            session.getMaps().remove(session.getActionsLogs().get(session.getActionsLogs().size() - 1).getMapOrSide());
            session.getActionsLogs().add(action);
        }

        if (actions.size() < 2) {
            session.setNextActionType(Action.BAN);
        } else if (actions.size() < 10) {
            if (actions.size() % 2 == 0) {
                session.setNextActionType(Action.PICK);
            } else {
                session.setNextActionType(Action.PICK_SIDE);
            }
        }

        if (maps.size() == 1) {
            String lastMap = maps.remove(0);
            session.getPickedMaps().add(lastMap);
            session.getSideSelections().add(new SideSelection(getRandomSide(), action.getTeam()));
            session.getActionsLogs().add(new PickBanAction("LEFT OVER", Action.PICK, lastMap));
            session.getActionsLogs().add(new PickBanAction("RANDOM SIDE", Action.PICK_SIDE,
                    session.getSideSelections().get(session.getSideSelections().size() - 1).getSide()));
            session.setCompleted(true);
            session.setNextActionType(null);
        }
    }

    private void startNewTimerIfNeeded(PickBanSession session) {
        if (session.getCurrentTimer() != null) {
            session.getCurrentTimer().cancel();
        }

        CustomTimerTask timerTask = new CustomTimerTask(() -> handleTimeout(session.getLobbyId()));

        Timer timer = new Timer();
        timer.schedule(timerTask, 100000);

        session.setCurrentTimer(timerTask);
    }


    private String getOppositeTeam(String team) {
        return "team2".equals(team) ? "team1" : "team2";
    }

    private void saveAndNotify(Lobby lobby) {
        if (lobby == null || lobby.getId() == null) {
            throw new IllegalArgumentException("Лобби или его ID не может быть null ");
        }

        String key = LOBBY_KEY_PREFIX + lobby.getId();

        try (Jedis jedis = jedisPool.getResource()) {
            String lobbyJson = JsonUtils.toJson(lobby);

            jedis.setex(key, 600, lobbyJson);
        } catch (Exception e) {
            log.info("Ошибка при сохранении лобби в Redis: {}", e.getMessage());
        }
    }

    private String getRandomSide() {
        return new Random().nextBoolean() ? "CT" : "T";
    }

    public String getTeamForCaptain(String steamId, Lobby lobby) {
        boolean isCaptainInTeam1 = lobby.getTeam1().stream()
                .anyMatch(user -> user.getSteamId().equals(steamId) && user.isCaptain());

        if (isCaptainInTeam1) {
            return "team1";
        }

        boolean isCaptainInTeam2 = lobby.getTeam2().stream()
                .anyMatch(user -> user.getSteamId().equals(steamId) && user.isCaptain());

        if (isCaptainInTeam2) {
            return "team2";
        }

        throw new IllegalStateException("Игрок с steamId " + steamId + " не является капитаном ни в одной из команд");
    }
}

