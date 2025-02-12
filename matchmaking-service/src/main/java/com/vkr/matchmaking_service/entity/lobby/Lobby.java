package com.vkr.matchmaking_service.entity.lobby;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vkr.matchmaking_service.dto.user.UserDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "lobby", timeToLive = 600)
@Jacksonized
public class Lobby implements Serializable {

    @Id
    private UUID id;

    @Indexed
    private String mode;

    private List<UserDto> team1 = new ArrayList<>();
    private List<UserDto> team2 = new ArrayList<>();

    @Indexed
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public int getMaxPlayersPerTeam() {
        return switch (mode) {
            case "1x1" -> 1;
            case "2x2" -> 2;
            default -> 5;
        };
    }

    public boolean isFull() {
        return team1.size() + team2.size() >= getMaxPlayersPerTeam() * 2;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

