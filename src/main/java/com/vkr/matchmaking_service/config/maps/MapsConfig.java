package com.vkr.matchmaking_service.config.maps;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.*;

@Setter
@Getter
@Component

public class MapsConfig {

    private Map<String, List<String>> maps;

    public MapsConfig() {
        maps = new HashMap<>();
        maps.put("1x1", Arrays.asList("3073259920", "3095343440", "3101654056", "3109027085", "3084291314", "3309764985", "3090032979"));
        maps.put("2x2", Arrays.asList("de_vertigo", "de_inferno", "de_overpass", "de_nuke", "3347582685", "3347606169", "3408016560"));
        maps.put("5x5", Arrays.asList("de_dust2", "de_inferno", "de_mirage", "de_nuke", "de_ancient", "de_train", "de_anubis"));
    }

    public List<String> getMapsByMode(String mode) {
        return maps.getOrDefault(mode, Collections.emptyList());
    }

}
