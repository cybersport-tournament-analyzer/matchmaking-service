package com.vkr.matchmaking_service.controller;


import com.vkr.matchmaking_service.dto.match.MatchPlayerDto;
import com.vkr.matchmaking_service.dto.match.MatchStartingDto;
import com.vkr.matchmaking_service.dto.match.StartMatchPlayerDto;
import com.vkr.matchmaking_service.dto.server.ConsoleLogDto;
import com.vkr.matchmaking_service.dto.server.ServerMetricsDto;
import com.vkr.matchmaking_service.dto.server.ServerSettingsDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;
import com.vkr.matchmaking_service.service.server.ServerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;


@RestController
@RequestMapping("/server")
@RequiredArgsConstructor
@Slf4j
public class ServerController {

    private final ServerService serverService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all servers")
    public Page<Server> findAll(Pageable pageable) throws IOException, InterruptedException {

        return serverService.getAllServers(pageable);
    }

    @GetMapping("/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get server by id")
    public Server getServerById(@PathVariable String serverId) throws IOException, InterruptedException {

        return serverService.getServerById(serverId);
    }

    @PostMapping("/start/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Start server")
    public String startServer(@PathVariable String serverId) throws IOException, InterruptedException {
        serverService.startServer(serverId);
        return serverService.getServerIp(serverId);
    }

    @PostMapping("/stop/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Stop server")
    public void stopServer(@PathVariable String serverId) throws IOException, InterruptedException {
        serverService.stopServer(serverId);
    }

    @PostMapping("/upload/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Upload file to server")
    public void uploadFileToServer(@PathVariable String serverId, @RequestParam String filePath, @RequestParam Path localFilePath) throws IOException, InterruptedException {
        serverService.uploadFileToServer(serverId, filePath, localFilePath);
    }

    @DeleteMapping("/delete/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete file from server")
    public void deleteFileFromServer(@PathVariable String serverId, @RequestParam String filePath) throws IOException, InterruptedException {
        serverService.deleteFileFromServer(serverId, filePath);
    }

    @PostMapping("/start-match")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Start match")
    public Match startMatch(@RequestBody MatchStartingDto matchStartingDto) throws IOException, InterruptedException {
        return serverService.startMatch(matchStartingDto);
    }

    @GetMapping("/match/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get match by id")
    public Match getMatchById(@PathVariable String matchId) throws IOException, InterruptedException {
        return serverService.getMatchById(matchId);
    }

    @PostMapping("/stop-match/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Stop match")
    public Match stopMatch(@PathVariable String matchId) throws IOException, InterruptedException {
        return serverService.stopMatch(matchId);
    }

    @PostMapping("/match/{matchId}/add-player")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add player to match")
    public MatchPlayerDto addPlayerToMatch(@PathVariable String matchId, @RequestBody StartMatchPlayerDto startMatchPlayerDto) throws IOException, InterruptedException {
        return serverService.addPlayerToMatch(matchId, startMatchPlayerDto);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update server")
    public void updateServer(@RequestBody ServerSettingsDto server) throws IOException, InterruptedException {
        serverService.updateServer(server);
    }

    @GetMapping("/metrics/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get server metrics by id")
    public ServerMetricsDto getServerMetrics(@PathVariable String serverId) throws IOException, InterruptedException {
        return serverService.getServerMetrics(serverId);
    }

    @GetMapping("/console/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get server console by id")
    public List<String> getServerMetrics(@PathVariable String serverId, @RequestParam int maxLines) throws IOException, InterruptedException {
        return serverService.getConsoleLogs(serverId, maxLines);
    }

}
