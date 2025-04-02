package com.vkr.matchmaking_service.service.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vkr.matchmaking_service.dto.match.MatchPlayerDto;
import com.vkr.matchmaking_service.dto.match.MatchStartingDto;
import com.vkr.matchmaking_service.dto.match.StartMatchPlayerDto;
import com.vkr.matchmaking_service.dto.server.ServerSettingsDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;
import com.vkr.matchmaking_service.exception.dao.MatchNotFoundException;
import com.vkr.matchmaking_service.exception.dao.ServerNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServerServiceImpl implements ServerService {

    @Value("${dathost.username}")
    private String username;
    @Value("${dathost.password}")
    private String password;

    private String auth;

    @Value("${dathost.matches-url}")
    private String matchesUrl;

    @Value("${dathost.servers-url}")
    private String serversUrl;

    @PostConstruct
    public void init() {
        auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    private final ObjectMapper objectMapper;

    @Override
    public Server getAvailableServer() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serversUrl))
                .header("accept", "application/json")
                .header("Authorization", "Basic " + auth)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        List<Server> servers = objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, Server.class));
        return servers.stream().filter(s -> !s.isOn()).findFirst().orElseThrow();
    }

    @Override
    public Page<Server> getAllServers(Pageable pageable) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serversUrl))
                .header("accept", "application/json")
                .header("Authorization", "Basic " + auth)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        log.info("Finding all servers");

        List<Server> servers = objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, Server.class));

        return new PageImpl<>(servers, pageable, servers.size());
    }

    @Override
    public void startServer(String serverId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serversUrl + "/" + serverId + "/start"))
                .header("content-type", "multipart/form-data")
                .header("Authorization", "Basic " + auth)
                .method("POST", HttpRequest.BodyPublishers.noBody())
                .build();
        Server server = getServerById(serverId);
        if (server == null) {
            throw new ServerNotFoundException("Server with id " + serverId + " not found!");
        }
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        log.info("CS2 server with id {} is started", serverId);
    }

    @Override
    public void stopServer(String serverId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serversUrl + "/" + serverId + "/stop"))
                .header("Authorization", "Basic " + auth)
                .method("POST", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        log.info("CS2 server with id {} is stopped", serverId);

    }

    @Override
    public Server getServerById(String serverId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serversUrl + "/" + serverId))
                .header("accept", "application/json")
                .header("Authorization", "Basic " + auth)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.body().isEmpty() && response.statusCode() != 200) {
            throw new ServerNotFoundException("Server with id " + serverId + " not found!");
        }

        log.info("Finding server with id {}", serverId);

        return objectMapper.readValue(response.body(), Server.class);
    }

    @Override
    public String getServerIp(String serverId) throws IOException, InterruptedException {
        Server server = getServerById(serverId);
        return "connect " + server.getIp() + ":" + server.getPorts().getGame();
    }

    @Override
    public Match startMatch(MatchStartingDto matchStartingDto) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(matchesUrl))
                .header("accept", "application/json")
                .header("Authorization", "Basic " + auth)
                .header("content-type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(matchStartingDto)))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.body().isEmpty() && response.statusCode() != 200) {
            throw new ServerNotFoundException("Server with id " + matchStartingDto.getGame_server_id() + " not found!");
        }

        log.info("Started match successfully: {}", response.body());

        return objectMapper.readValue(response.body(), Match.class);
    }

    @Override
    public Match getMatchById(String matchId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(matchesUrl + "/" + matchId))
                .header("accept", "application/json")
                .header("Authorization", "Basic " + auth)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.body().isEmpty() && response.statusCode() != 200) {
            throw new MatchNotFoundException("Match with id " + matchId + " not found");
        }

        return objectMapper.readValue(response.body(), Match.class);
    }

    @Override
    public Match stopMatch(String matchId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(matchesUrl + matchId + "/cancel"))
                .header("accept", "application/json")
                .header("Authorization", "Basic " + auth)
                .method("POST", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.body().isEmpty() && response.statusCode() != 200) {
            throw new MatchNotFoundException("Match with id " + matchId + " not found");
        }

        return objectMapper.readValue(response.body(), Match.class);
    }

    @Override
    public MatchPlayerDto addPlayerToMatch(String matchId, StartMatchPlayerDto startMatchPlayerDto) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(matchesUrl + "/" + matchId + "/players"))
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Basic " + auth)
                .method("POST", HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(startMatchPlayerDto)))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.body().isEmpty() && response.statusCode() != 200) {
            throw new MatchNotFoundException("Match with id " + matchId + " not found");
        }

        return objectMapper.readValue(response.body(), MatchPlayerDto.class);
    }

    @Override
    public void updateServer(ServerSettingsDto serverSettingsDto) throws IOException, InterruptedException {


        String req = String.format(
                "-----011000010111000001101001\r\n" +
                        "Content-Disposition: form-data; name=\"cs2_settings.game_mode\"\r\n\r\n" +
                        "%s\r\n" +
                        "-----011000010111000001101001\r\n" +
                        "Content-Disposition: form-data; name=\"cs2_settings.maps_source\"\r\n\r\n" +
                        "%s\r\n" +
                        "-----011000010111000001101001\r\n" +
                        "Content-Disposition: form-data; name=\"cs2_settings.%s\"\r\n\r\n" +
                        "%s\r\n" +
                        "-----011000010111000001101001--",
                serverSettingsDto.getCs2_settings().getGame_mode(),
                (serverSettingsDto.getCs2_settings().getMaps_source()),
                serverSettingsDto.getCs2_settings().getMaps_source().equals("mapgroup") ? "mapgroup_start_map" : "workshop_single_map_id",
                serverSettingsDto.getCs2_settings().getMaps_source().equals("mapgroup") ? serverSettingsDto.getCs2_settings().getMapgroup_start_map() : serverSettingsDto.getCs2_settings().getWorkshop_single_map_id());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serversUrl + "/" + serverSettingsDto.getServer_id()))
                .header("Authorization", "Basic " + auth)
                .header("content-type", "multipart/form-data; boundary=---011000010111000001101001")
                .PUT(HttpRequest.BodyPublishers.ofString(req))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Server updated successfully.");
        } else {
            System.err.println("Failed to update game mode. Status code: " + response.statusCode());
            System.err.println("Response: " + response.body());
        }
    }

}
