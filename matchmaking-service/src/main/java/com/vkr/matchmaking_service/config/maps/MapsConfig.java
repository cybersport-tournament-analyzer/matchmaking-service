package com.vkr.matchmaking_service.config.maps;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.*;

@Setter
@Getter
@Component
//@ConfigurationProperties(prefix = "maps")
public class MapsConfig {

    private Map<String, List<String>> maps;

    public MapsConfig() {
        maps = new HashMap<>();
        maps.put("1x1", Arrays.asList("2939997647", "3429229778", "3197575080", "3428048636", "de_dust2", "de_inferno", "de_mirage", "de_nuke", "de_ancient", "de_train", "de_anubis", "de_overpass"));
        maps.put("2x2", Arrays.asList("de_dust2", "de_inferno", "de_mirage", "de_nuke", "de_ancient", "de_train", "de_anubis", "de_overpass"));
        maps.put("5x5", Arrays.asList("de_dust2", "de_inferno", "de_mirage", "de_nuke", "de_ancient", "de_train", "de_anubis"));
    }

    public List<String> getMapsByMode(String mode) {
        return maps.getOrDefault(mode, Collections.emptyList());
    }

}
