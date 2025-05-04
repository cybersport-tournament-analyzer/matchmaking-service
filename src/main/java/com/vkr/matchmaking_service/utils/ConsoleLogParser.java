package com.vkr.matchmaking_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vkr.matchmaking_service.dto.stats.KillEventDto;
import com.vkr.matchmaking_service.dto.stats.RoundEndReasonDto;
import com.vkr.matchmaking_service.dto.stats.RoundStatsDto;
import com.vkr.matchmaking_service.entity.match.Match;
import com.vkr.matchmaking_service.kafka.event.roundEnd.RoundEndEvent;
import com.vkr.matchmaking_service.mapper.RoundStatsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConsoleLogParser {

    private static final Pattern JSON_START = Pattern.compile("JSON_BEGIN\\{");
    private static final Pattern JSON_END = Pattern.compile("}}JSON_END");
    private static final Pattern SFUI_NOTICE_PATTERN =
            Pattern.compile("Team \"(.*?)\" triggered \"(SFUI_Notice_.*?)\" \\(CT \"(\\d+)\"\\) \\(T \"(\\d+)\"\\)");
    private static final Pattern KILL_EVENT_PATTERN = Pattern.compile(
            "\"(.+?)<\\d+><(\\[U:1:\\d+])><(.*?)>\" \\[.*?\\] killed " +
                    "\"(.+?)<\\d+><(\\[U:1:\\d+])><(.*?)>\" \\[.*?\\] with \"(\\w+)\"(?: \\((.*?)\\))?"
    );
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RoundStatsMapper roundStatsMapper;

    public RoundEndEvent parseRoundEnd(List<String> allLogLines, Match match, UUID tournamentMatchId, UUID tournamentId) {
        int isFinalRound = isFinal(allLogLines);
        List<String> logLines = extractRelevantLines(allLogLines);
        RoundStatsDto statsDto = extractRoundStats(logLines, match.getRounds_played());

        assert statsDto != null;
        Map<String, String> replaces = replaceAccountIdsWithSteamIds(statsDto.getPlayers(), match.getPlayers());

        for (Map<String, String> player : statsDto.getPlayers()) {
            String accountId = player.get("accountid");
            if (accountId != null && replaces.containsKey(accountId)) {
                player.put("accountid", replaces.get(accountId));
            }
        }

        for (Map.Entry<String, String> entry : replaces.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }

        for (Map<String, String> player : statsDto.getPlayers()) {
            for (Map.Entry<String, String> entry : player.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
        }

        RoundEndReasonDto reasonDto = extractRoundEndReason(logLines);
        List<KillEventDto> killEvents = extractKillEvents(logLines, replaces);

        return RoundEndEvent.builder()
                .tournamentMatchId(tournamentMatchId)
                .tournamentId(tournamentId)
                .roundStats(statsDto)
                .roundEndReason(reasonDto)
                .killEvents(killEvents)
                .isFinal(isFinalRound)
                .match(match)
                .build();
    }

    private Map<String, String> replaceAccountIdsWithSteamIds(List<Map<String, String>> playersFromConsole, List<Match.Player> playersFromMatch) {
        Map<String, String> replaces = new HashMap<>();
        for (int i = 0; i < playersFromConsole.size(); i++) {
            Map<String, String> consoleStats = playersFromConsole.get(i);
            Match.Player matchPlayer = playersFromMatch.get(i);
            replaces.put(consoleStats.get("accountid"), matchPlayer.getSteam_id_64());
        }
        return replaces;
    }

    private List<String> extractRelevantLines(List<String> lines) {
        List<String> relevant = new ArrayList<>();
        int startIndex = -1;

        for (int i = lines.size() - 1; i >= 0; i--) {
            if (lines.get(i).contains("JSON_BEGIN{")) {
                startIndex = i;
                break;
            }
        }

        if (startIndex != -1) {
            relevant = new ArrayList<>(lines.subList(startIndex, lines.size()));
        }

        return relevant;
    }

    private RoundStatsDto extractRoundStats(List<String> logLines, int currentRound) {
        boolean insideJson = false;
        StringBuilder jsonBuilder = new StringBuilder();

        for (int i = 0; i < logLines.size(); i++) {
            String line = logLines.get(i);

            if (JSON_START.matcher(line).find()) {
                insideJson = true;
                jsonBuilder.setLength(0);
                jsonBuilder.append("{");
            } else if (insideJson) {
                if (JSON_END.matcher(line).find()) {
                    jsonBuilder.append("}}");

                    try {
                        Map<String, Object> rawMap = objectMapper.readValue(jsonBuilder.toString(), Map.class);
                        int roundNumber = Integer.parseInt((String) rawMap.get("round_number"));

                        if (roundNumber == currentRound) {
                            return roundStatsMapper.fromMap(rawMap);
                        }
                    } catch (Exception e) {
                        log.warn("Ошибка парсинга round_stats JSON: {}", e.getMessage());
                        log.warn("Собранный JSON:\n{}", jsonBuilder);
                    }

                    insideJson = false;
                } else {
                    line = line.replaceFirst("^.*?L \\d{2}/\\d{2}/\\d{4} - \\d{2}:\\d{2}:\\d{2}: ", "").trim();

                    if (line.startsWith("\"players\"")) {
                        int lastNewline = jsonBuilder.lastIndexOf("\n");
                        if (lastNewline != -1 && jsonBuilder.charAt(lastNewline - 1) != ',') {
                            jsonBuilder.insert(lastNewline, ",");
                        }
                    }

                    if (line.startsWith("\"player_")) {
                        if (i + 1 < logLines.size() && !JSON_END.matcher(logLines.get(i + 1)).find()) {
                            line += ",";
                        }
                    }

                    jsonBuilder.append(line).append("\n");
                }
            }
        }

        return null;
    }

    private RoundEndReasonDto extractRoundEndReason(List<String> logLines) {
        for (int i = logLines.size() - 1; i >= 0; i--) {
            Matcher matcher = SFUI_NOTICE_PATTERN.matcher(logLines.get(i));
            if (matcher.find()) {
                return RoundEndReasonDto.builder()
                        .winningTeam(matcher.group(1))
                        .reasonCode(matcher.group(2))
                        .scoreCT(Integer.parseInt(matcher.group(3)))
                        .scoreT(Integer.parseInt(matcher.group(4)))
                        .build();
            }
        }
        return null;
    }

    private List<KillEventDto> extractKillEvents(List<String> logLines, Map<String, String> replaces) {
        List<KillEventDto> events = new ArrayList<>();
        for (String line : logLines) {
            Matcher m = KILL_EVENT_PATTERN.matcher(line);
            if (m.find()) {
                String killerAccountId = m.group(2);
                killerAccountId = killerAccountId.replace("[U:1:", "");
                killerAccountId = killerAccountId.replace("]", "");

                String victimAccountId = m.group(5);
                victimAccountId = victimAccountId.replace("[U:1:", "");
                victimAccountId = victimAccountId.replace("]", "");

                String killerSteamId = replaces.get(killerAccountId);
                System.out.println(killerSteamId);
                String victimSteamId = replaces.get(victimAccountId);
                System.out.println(victimSteamId);

                events.add(KillEventDto.builder()
                        .killerName(m.group(1))
                        .killerSteamId(killerSteamId)
                        .killerTeam(m.group(3))
                        .victimName(m.group(4))
                        .victimSteamId(victimSteamId)
                        .victimTeam(m.group(6))
                        .weapon(m.group(7))
                        .headshot(m.group(8) != null && m.group(8).contains("headshot"))
                        .penetrated(m.group(8) != null && m.group(8).contains("penetrated"))
                        .noscope(m.group(8) != null && m.group(8).contains("noscope"))
                        .smoke(m.group(8) != null && m.group(8).contains("throughsmoke"))
                        .build());
            }
        }
        return events;
    }

    private int isFinal(List<String> logLines) {
        if (logLines == null) return 1;

        for (String line : logLines) {
            if (line.contains("ACCOLADE, FINAL") || line.contains("Game Over:")) {
                return 2;
            }
        }
        return 1;
    }
}