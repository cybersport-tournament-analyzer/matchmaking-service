package com.vkr.matchmaking_service.mapper;

import com.vkr.matchmaking_service.dto.stats.RoundStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RoundStatsMapper {

    public RoundStatsDto fromMap(Map<String, Object> rawMap) {
        String[] fieldsArray = ((String) rawMap.get("fields")).replaceAll("\\s+", "").split(",");
        List<String> fields = Arrays.asList(fieldsArray);

        Map<String, String> rawPlayers = (Map<String, String>) rawMap.get("players");
        List<Map<String, String>> parsedPlayers = new ArrayList<>();

        for (String valueLine : rawPlayers.values()) {
            String[] values = valueLine.trim().split("\\s*,\\s*");
            Map<String, String> playerMap = new LinkedHashMap<>();
            for (int i = 0; i < fields.size() && i < values.length; i++) {
                playerMap.put(fields.get(i), values[i]);
            }
            parsedPlayers.add(playerMap);
        }

        return RoundStatsDto.builder()
                .roundNumber(Integer.parseInt((String) rawMap.get("round_number")))
                .scoreT(Integer.parseInt((String) rawMap.get("score_t")))
                .scoreCT(Integer.parseInt((String) rawMap.get("score_ct")))
                .map((String) rawMap.get("map"))
                .server((String) rawMap.get("server"))
                .fields(fields)
                .players(parsedPlayers)
                .build();
    }
}