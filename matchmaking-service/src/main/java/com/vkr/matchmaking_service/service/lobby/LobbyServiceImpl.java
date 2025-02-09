package com.vkr.matchmaking_service.service.lobby;

import com.vkr.matchmaking_service.client.UserServiceClient;
import com.vkr.matchmaking_service.dto.user.UserDto;
import com.vkr.matchmaking_service.entity.server.Lobby;
import com.vkr.matchmaking_service.exception.LobbyIsFullException;
import com.vkr.matchmaking_service.exception.LobbyNotFoundException;
import com.vkr.matchmaking_service.exception.TeamIsFullException;
import com.vkr.matchmaking_service.exception.WrongInputException;
import com.vkr.matchmaking_service.repository.LobbyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LobbyServiceImpl implements LobbyService {

    private final LobbyRepository lobbyRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public List<Lobby> getAllLobbies() {
        return lobbyRepository.findAll();
    }

    @Override
    public Lobby createLobby(String mode, String steamId) {
        if (!List.of("1x1", "2x2", "5x5").contains(mode)) {
            throw new WrongInputException("Wrong game mode!");
        }

        UserDto creator = userServiceClient.getUserBySteamId(steamId);
        Lobby lobby = new Lobby();
        lobby.setMode(mode);
        lobby.getTeam1().add(creator);
        return lobbyRepository.save(lobby);
    }


    @Override
    @Transactional
    public void addPlayer(UUID lobbyId, String steamId, String team) {
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby not found!"));

        if (lobby.isFull()) {
            throw new LobbyIsFullException("Lobby is full!");
        }

        List<UserDto> targetTeam = team.equals("team1") ? lobby.getTeam1() : lobby.getTeam2();

        if (targetTeam.size() >= lobby.getMaxPlayersPerTeam()) {
            throw new TeamIsFullException("Chosen team is full!");
        }

        UserDto currentPlayer = userServiceClient.getUserBySteamId(steamId);
        targetTeam.add(currentPlayer);
    }

    @Override
    @Transactional
    public void removePlayer(UUID lobbyId, String steamId) {
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby not found!"));
        UserDto player = userServiceClient.getUserBySteamId(steamId);
        if (lobby.getTeam1().contains(player)) {
            lobby.getTeam1().remove(player);
        } else {
            lobby.getTeam2().remove(player);
        }
        lobbyRepository.save(lobby);

        if (lobby.getTeam1().isEmpty() && lobby.getTeam2().isEmpty()) {
            lobbyRepository.delete(lobby);
        }
    }

    @Override
    public void removeExpiredLobbies(List<Lobby> lobbies) {
        lobbyRepository.deleteAll(lobbies);
    }

    @Override
    public Lobby getLobbyById(String lobbyId) {
        return lobbyRepository.findById(UUID.fromString(lobbyId)).orElseThrow(() -> new LobbyNotFoundException("Lobby not found!"));
    }
}
