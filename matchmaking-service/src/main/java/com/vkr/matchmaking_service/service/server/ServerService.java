package com.vkr.matchmaking_service.service.server;

import com.vkr.matchmaking_service.entity.server.Server;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface ServerService {

    Page<Server> getAllServers(Pageable pageable) throws IOException, InterruptedException;

    void startServer(String serverId) throws IOException, InterruptedException;

    void stopServer(String serverId) throws IOException, InterruptedException;

    Server getServerById(String serverId) throws IOException, InterruptedException;

    String getServerIp(String serverId) throws IOException, InterruptedException;
}
