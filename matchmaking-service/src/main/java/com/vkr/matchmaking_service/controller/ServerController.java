package com.vkr.matchmaking_service.controller;


import com.vkr.matchmaking_service.exception.ServerNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/server")
@RequiredArgsConstructor
@Slf4j
public class ServerController {

    @Value("${dathost.username}")
    private String username;
    @Value("${dathost.password}")
    private String password;

    private String auth;

    @PostConstruct
    public void init() {
        auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all servers")
    public String findAll() throws IOException, InterruptedException {
        System.out.println(username + " " + password);
        System.out.println(auth);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dathost.net/api/0.1/game-servers"))
                .header("accept", "application/json")
                .header("Authorization", "Basic " + auth)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        log.info("Finding all servers");
        return response.body();
    }

    @GetMapping("/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get server by id")
    public String getServerById(@PathVariable String serverId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dathost.net/api/0.1/game-servers/" + serverId))
                .header("accept", "application/json")
                .header("Authorization", "Basic " + auth)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.body().isEmpty()) {
            throw new ServerNotFoundException("Server with id " + serverId + " not found!");
        }
        log.info("Finding server with id {}", serverId);
        return response.body();
    }

    @PostMapping("/start/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Start server")
    public void startServer(@PathVariable String serverId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dathost.net/api/0.1/game-servers/" + serverId + "/start"))
                .header("content-type", "multipart/form-data")
                .header("Authorization", "Basic " + auth)
                .method("POST", HttpRequest.BodyPublishers.noBody())
                .build();
        if (getServerById(serverId).isEmpty()) {
            throw new ServerNotFoundException("Server with id " + serverId + " not found!");
        }
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        log.info("CS2 server with id {} is started", serverId);
    }

    @PostMapping("/stop/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Stop server")
    public void stopServer(@PathVariable String serverId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dathost.net/api/0.1/game-servers/" + serverId + "/stop"))
                .method("POST", HttpRequest.BodyPublishers.noBody())
                .build();
        if (getServerById(serverId).isEmpty()) {
            throw new ServerNotFoundException("Server with id " + serverId + " not found!");
        }
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        log.info("CS2 server with id {} is stopped", serverId);
    }

}
