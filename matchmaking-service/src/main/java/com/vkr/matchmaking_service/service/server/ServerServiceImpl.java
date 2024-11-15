package com.vkr.matchmaking_service.service.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vkr.matchmaking_service.entity.server.Server;
import com.vkr.matchmaking_service.exception.ServerNotFoundException;
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

    @Value("${dathost.url}")
    private String url;

    @PostConstruct
    public void init() {
        auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    private final ObjectMapper objectMapper;

    @Override
    public Page<Server> getAllServers(Pageable pageable) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
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
                .uri(URI.create(url + "/" + serverId + "/start"))
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
                .uri(URI.create(url + "/" + serverId + "/stop"))
                .header("Authorization", "Basic " + auth)
                .method("POST", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        log.info("CS2 server with id {} is stopped", serverId);

    }

    @Override
    public Server getServerById(String serverId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + serverId))
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

}
