package com.vkr.matchmaking_service.controller;


import com.vkr.matchmaking_service.exception.ServerNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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
        if(response.body().isEmpty()){
            throw new ServerNotFoundException("Сервер с данным id не найден!");
        }
        return response.body();
    }
}
