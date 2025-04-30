package com.vkr.matchmaking_service.service.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vkr.matchmaking_service.dto.match.MatchPlayerDto;
import com.vkr.matchmaking_service.dto.match.MatchStartingDto;
import com.vkr.matchmaking_service.dto.match.StartMatchPlayerDto;
import com.vkr.matchmaking_service.dto.server.ConsoleLogDto;
import com.vkr.matchmaking_service.dto.server.ServerMetricsDto;
import com.vkr.matchmaking_service.dto.server.ServerSettingsDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.entity.server.Server;
import com.vkr.matchmaking_service.exception.MatchNotFoundException;
import com.vkr.matchmaking_service.exception.ServerNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncServerServiceImpl implements AsyncServerService {

    @Value("${dathost.username}")
    private String username;
    @Value("${dathost.password}")
    private String password;

    public String auth;

    @Value("${dathost.matches-url}")
    public String matchesUrl;

    @Value("${dathost.servers-url}")
    public String serversUrl;

    @PostConstruct
    public void init() {
        auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
    }

    private final AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
    private final ObjectMapper objectMapper;


    @Override
    public CompletableFuture<Server> getAvailableServer() {

        CompletableFuture<Server> future = new CompletableFuture<>();

        Runnable checkServer = new Runnable() {
            @Override
            public void run() {
                asyncHttpClient.prepareGet(serversUrl)
                        .addHeader("accept", "application/json")
                        .addHeader("Authorization", "Basic " + auth)
                        .execute()
                        .toCompletableFuture()
                        .thenApply(response -> {
                            try {
                                List<Server> servers = objectMapper.readValue(
                                        response.getResponseBody(),
                                        objectMapper.getTypeFactory().constructCollectionType(List.class, Server.class)
                                );
                                return servers.stream()
                                        .filter(s -> !s.isOn())
                                        .findFirst();
                            } catch (JsonProcessingException e) {
                                throw new CompletionException(e);
                            }
                        })
                        .thenCompose(serverOpt -> {
                            if (serverOpt.isPresent()) {
                                future.complete(serverOpt.get());
                            } else {
                                log.info("Ищу сервер...");
                                CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS)
                                        .execute(this);
                            }
                            return future;
                        })
                        .exceptionally(ex -> {
                            future.completeExceptionally(ex);
                            return null;
                        });
            }
        };

        checkServer.run();
        return future;
    }

    @Override
    public CompletableFuture<PageImpl<Server>> getAllServers(Pageable pageable) {

        return asyncHttpClient.prepareGet(serversUrl)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Basic " + auth)
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    try {
                        List<Server> servers = objectMapper.readValue(
                                response.getResponseBody(),
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Server.class)
                        );
                        log.info("Found {} servers", servers.size());
                        return new PageImpl<>(servers, pageable, servers.size());
                    } catch (JsonProcessingException e) {
                        throw new CompletionException(e);
                    }
                })
                .exceptionally(ex -> {
                    log.error("Failed to get servers: {}", ex.getMessage());
                    throw new CompletionException(ex);
                });
    }

    @Override
    public CompletableFuture<Void> startServer(String serverId) {

        return getServerById(serverId)
                .thenCompose(server -> {
                    String startUrl = serversUrl + "/" + serverId + "/start";

                    return asyncHttpClient.preparePost(startUrl)
                            .addHeader("content-type", "multipart/form-data")
                            .addHeader("Authorization", "Basic " + auth)
                            .setBody(new byte[0])
                            .execute()
                            .toCompletableFuture()
                            .thenAccept(response -> {
                                if (response.getStatusCode() >= 300) {
                                    throw new CompletionException(
                                            new IOException("Failed to start server: HTTP " + response.getStatusCode())
                                    );
                                }
                                log.info("CS2 server with id {} is started", serverId);
                            });
                })
                .exceptionally(ex -> {
                    if (ex.getCause() instanceof ServerNotFoundException) {
                        log.error("Server not found: {}", serverId);
                    } else {
                        log.error("Failed to start server {}: {}", serverId, ex.getMessage());
                    }
                    return null;
                });
    }

    @Override
    public CompletableFuture<Void> stopServer(String serverId) {
        String stopUrl = serversUrl + "/" + serverId + "/stop";

        return asyncHttpClient.prepare("POST", stopUrl)
                .setHeader("Authorization", "Basic " + auth)
                .setBody("")
                .execute()
                .toCompletableFuture()
                .thenAccept(response -> {
                    if (response.getStatusCode() >= 300) {
                        throw new RuntimeException("Failed to stop server: HTTP " + response.getStatusCode());
                    }
                    log.info("CS2 server with id {} is stopped", serverId);
                })
                .exceptionally(ex -> {
                    log.error("Error stopping server {}: {}", serverId, ex.getMessage());
                    return null;
                });

    }

    @Override
    public CompletableFuture<Void> uploadFileToServer(String serverId, String filePath, Path localFilePath) throws IOException {
        Path localPath = Path.of(localFilePath.toUri());
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        byte[] fileBytes = Files.readAllBytes(localPath);
        String fileName = localPath.getFileName().toString();
        return CompletableFuture.supplyAsync(() -> {
            String formData = "--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n" +
                    "Content-Type: application/octet-stream\r\n\r\n";

            return concatBytes(
                    formData.getBytes(),
                    fileBytes,
                    ("\r\n--" + boundary + "--\r\n").getBytes()
            );
        }).thenCompose(multipartBody -> asyncHttpClient.prepare("POST", serversUrl + "/" + serverId + "/files/" + filePath)
                .addHeader("Content-Type", "multipart/form-data; boundary=" + boundary)
                .addHeader("Authorization", "Basic " + auth)
                .setBody(multipartBody)
                .execute()
                .toCompletableFuture()
                .thenAccept(response -> {
                    log.info("File {} was uploaded to server", fileName);
                }));
    }

    @Override
    public CompletableFuture<Void> deleteFileFromServer(String serverId, String filePath) {

        return asyncHttpClient.prepareDelete(serversUrl + "/" + serverId + "/files/" + filePath)
                .addHeader("Authorization", "Basic " + auth)
                .execute()
                .toCompletableFuture()
                .thenAccept(response -> {
                    log.info("File with path {} was deleted from server", filePath);
                })
                .exceptionally(ex -> {
                    log.error("Failed to delete file: {}", ex.getMessage());
                    return null;
                });
    }

    @Override
    public CompletableFuture<Server> getServerById(String serverId) {

        return asyncHttpClient.prepareGet(serversUrl + "/" + serverId)
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Basic " + auth)
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    if (response.getStatusCode() != 200 || response.getResponseBody().isEmpty()) {
                        throw new CompletionException(
                                new ServerNotFoundException("Server with id " + serverId + " not found!")
                        );
                    }

                    log.info("Found server with id {}", serverId);

                    try {
                        return objectMapper.readValue(response.getResponseBody(), Server.class);
                    } catch (JsonProcessingException e) {
                        throw new CompletionException(e);
                    }
                });
    }

    @Override
    public CompletableFuture<String> getServerIp(String serverId) {
        return getServerById(serverId)
                .thenApply(server -> "connect " + server.getIp() + ":" + server.getPorts().getGame())
                .exceptionally(ex -> {
                    throw new CompletionException(
                            new ServerNotFoundException("Server with id " + serverId + " not found!")
                    );
                });
    }

    @Override
    public CompletableFuture<Match> startMatch(MatchStartingDto matchStartingDto) {

        try {
            String requestBody = objectMapper.writeValueAsString(matchStartingDto);

            return asyncHttpClient.preparePost(matchesUrl)
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Basic " + auth)
                    .addHeader("content-type", "application/json")
                    .setBody(requestBody)
                    .execute()
                    .toCompletableFuture()
                    .thenApply(response -> {
                        if (response.getStatusCode() != 200 || response.getResponseBody().isEmpty()) {
                            throw new CompletionException(
                                    new ServerNotFoundException("Server not found: " + matchStartingDto.getGame_server_id())
                            );
                        }
                        try {
                            Match match = objectMapper.readValue(response.getResponseBody(), Match.class);
                            log.info("Started match successfully: {}", match.getId());
                            return match;
                        } catch (JsonProcessingException e) {
                            throw new CompletionException(e);
                        }
                    });
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(e);
        }
    }


    @Override
    public CompletableFuture<Match> stopMatch(String matchId) {

        return asyncHttpClient.prepare("POST", matchesUrl + matchId + "/cancel")
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Basic " + auth)
                .setBody("")
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    if (response.getStatusCode() != 200 || response.getResponseBody().isEmpty()) {
                        throw new CompletionException(
                                new MatchNotFoundException("Match with id " + matchId + " not found")
                        );
                    }
                    try {
                        return objectMapper.readValue(response.getResponseBody(), Match.class);
                    } catch (JsonProcessingException e) {
                        throw new CompletionException(e);
                    }
                });
    }

    @Override
    public CompletableFuture<Void> updateServer(ServerSettingsDto serverSettingsDto) {

        String boundary = "---011000010111000001101001";
        String reqBody = String.format(
                "%s\r\n" +
                        "Content-Disposition: form-data; name=\"cs2_settings.game_mode\"\r\n\r\n" +
                        "%s\r\n" +
                        "%s\r\n" +
                        "Content-Disposition: form-data; name=\"cs2_settings.maps_source\"\r\n\r\n" +
                        "%s\r\n" +
                        "%s\r\n" +
                        "Content-Disposition: form-data; name=\"cs2_settings.%s\"\r\n\r\n" +
                        "%s\r\n" +
                        "%s--",
                boundary,
                serverSettingsDto.getCs2_settings().getGame_mode(),
                boundary,
                serverSettingsDto.getCs2_settings().getMaps_source(),
                boundary,
                serverSettingsDto.getCs2_settings().getMaps_source().equals("mapgroup") ? "mapgroup_start_map" : "workshop_single_map_id",
                serverSettingsDto.getCs2_settings().getMaps_source().equals("mapgroup") ?
                        serverSettingsDto.getCs2_settings().getMapgroup_start_map() :
                        serverSettingsDto.getCs2_settings().getWorkshop_single_map_id(),
                boundary
        );

        return asyncHttpClient.prepare("PUT", serversUrl + "/" + serverSettingsDto.getServer_id())
                .addHeader("Authorization", "Basic " + auth)
                .addHeader("content-type", "multipart/form-data; boundary=" + boundary)
                .setBody(reqBody)
                .execute()
                .toCompletableFuture()
                .thenAccept(response -> {
                    if (response.getStatusCode() == 200) {
                        log.info("Server updated successfully");
                    } else {
                        log.error("Failed to update server. Status: {}\nResponse: {}",
                                response.getStatusCode(), response.getResponseBody());
                        throw new CompletionException(new IOException("Server update failed"));
                    }
                });
    }

    @Override
    public CompletableFuture<List<String>> getConsoleLogs(String serverId, int maxLines) {
        String uri = String.format("%s/%s/console?max_lines=%d", serversUrl, serverId, maxLines);

        return asyncHttpClient.prepareGet(uri)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", "Basic " + auth)
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    if (response.getStatusCode() != 200 || response.getResponseBody().isEmpty()) {
                        throw new CompletionException(
                                new ServerNotFoundException("Server with id " + serverId + " not found")
                        );
                    }
                    try {
                        return objectMapper.readValue(response.getResponseBody(), ConsoleLogDto.class).getLines();
                    } catch (JsonProcessingException e) {
                        throw new CompletionException(e);
                    }
                });
    }

    private static byte[] concatBytes(byte[]... arrays) {
        int length = 0;
        for (byte[] arr : arrays) length += arr.length;
        byte[] result = new byte[length];

        int pos = 0;
        for (byte[] arr : arrays) {
            System.arraycopy(arr, 0, result, pos, arr.length);
            pos += arr.length;
        }
        return result;
    }
}
