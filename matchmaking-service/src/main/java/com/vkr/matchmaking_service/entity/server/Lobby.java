package com.vkr.matchmaking_service.entity.server;

import com.vkr.matchmaking_service.dto.user.UserDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lobby")
public class Lobby {
    @Id
    @UuidGenerator
    private UUID id;

    @ElementCollection
    private List<UserDto> team1 = new ArrayList<>();

    @ElementCollection
    private List<UserDto> team2 = new ArrayList<>();


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(nullable = false)
    private String mode;

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

}

