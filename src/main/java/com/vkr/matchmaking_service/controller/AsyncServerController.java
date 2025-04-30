package com.vkr.matchmaking_service.controller;

import com.vkr.matchmaking_service.dto.match.MatchStartingDto;
import com.vkr.matchmaking_service.dto.server.ServerSettingsDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;
import com.vkr.matchmaking_service.service.server.AsyncServerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/servers/async")
@RequiredArgsConstructor
@Slf4j
public class AsyncServerController {
    private final AsyncServerService asyncServerService;

    @PostMapping("/stop/{serverId}")
    public CompletableFuture<Void> stopServer(@PathVariable String serverId) throws IOException, InterruptedException {
        return asyncServerService.stopServer(serverId);
    }

    @GetMapping("/{serverId}")
    public CompletableFuture<Server> getServer(@PathVariable String serverId) throws IOException, InterruptedException {
        return asyncServerService.getServerById(serverId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all servers")
    public CompletableFuture<PageImpl<Server>> findAll(Pageable pageable) throws IOException, InterruptedException {

        return asyncServerService.getAllServers(pageable);
    }

    @PostMapping("/start/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Start server")
    public CompletableFuture<String> startServer(@PathVariable String serverId) throws IOException, InterruptedException {
        asyncServerService.startServer(serverId);
        return asyncServerService.getServerIp(serverId);
    }

    @PostMapping("/upload/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Upload file to server")
    public CompletableFuture<Void> uploadFileToServer(@PathVariable String serverId, @RequestParam String filePath, @RequestParam Path localFilePath) throws IOException, InterruptedException {
        return asyncServerService.uploadFileToServer(serverId, filePath, localFilePath);
    }

    @DeleteMapping("/delete/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete file from server")
    public CompletableFuture<Void> deleteFileFromServer(@PathVariable String serverId, @RequestParam String filePath) throws IOException, InterruptedException {
        return asyncServerService.deleteFileFromServer(serverId, filePath);
    }

    @PostMapping("/start-match")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Start match")
    public CompletableFuture<Match> startMatch(@RequestBody MatchStartingDto matchStartingDto) throws IOException, InterruptedException {
        return asyncServerService.startMatch(matchStartingDto);
    }

    @PostMapping("/stop-match/{matchId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Stop match")
    public CompletableFuture<Match> stopMatch(@PathVariable String matchId) throws IOException, InterruptedException {
        return asyncServerService.stopMatch(matchId);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update server")
    public CompletableFuture<Void> updateServer(@RequestBody ServerSettingsDto server) throws IOException, InterruptedException {
        return asyncServerService.updateServer(server);
    }

    @GetMapping("/console/{serverId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get server console by id")
    public CompletableFuture<List<String>> getConsoleLogs(@PathVariable String serverId, @RequestParam int maxLines) throws IOException, InterruptedException {
        return asyncServerService.getConsoleLogs(serverId, maxLines);
    }
}
