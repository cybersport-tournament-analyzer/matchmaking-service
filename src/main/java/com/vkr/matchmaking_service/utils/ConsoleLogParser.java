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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public RoundEndEvent parseRoundEnd(List<String> allLogLines, Match match) {
        List<String> logLines = extractRelevantLines(allLogLines);
        RoundStatsDto statsDto = extractRoundStats(logLines, match.getRounds_played());
        RoundEndReasonDto reasonDto = extractRoundEndReason(logLines);
        List<KillEventDto> killEvents = extractKillEvents(logLines);

        return RoundEndEvent.builder()
                .roundStats(statsDto)
                .roundEndReason(reasonDto)
                .killEvents(killEvents)
                .build();
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

    private List<KillEventDto> extractKillEvents(List<String> logLines) {
        List<KillEventDto> events = new ArrayList<>();
        for (String line : logLines) {
            Matcher m = KILL_EVENT_PATTERN.matcher(line);
            if (m.find()) {
                events.add(KillEventDto.builder()
                        .killerName(m.group(1))
                        .killerSteamId(m.group(2))
                        .killerTeam(m.group(3))
                        .victimName(m.group(4))
                        .victimSteamId(m.group(5))
                        .victimTeam(m.group(6))
                        .weapon(m.group(7))
                        .headshot(m.group(8) != null && m.group(8).contains("headshot"))
                        .penetrated(m.group(8) != null && m.group(8).contains("penetrated"))
                        .noscope(m.group(8) != null && m.group(8).contains("noscope"))
                        .build());
            }
        }
        return events;
    }
}