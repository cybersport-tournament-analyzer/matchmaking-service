package com.vkr.matchmaking_service.config.maps;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Component
public class MapsConfig {
    private final Map<String, Map<String, String>> maps;

    public MapsConfig() {
        maps = new HashMap<>();

        Map<String, String> mode1x1 = new HashMap<>();
        mode1x1.put("AIM_Fake", "3073259920");
        mode1x1.put("AIM_Garage", "3095343440");
        mode1x1.put("AIM_Fist", "3101654056");
        mode1x1.put("AWP_Minecraft", "3109027085");
        mode1x1.put("AIM_Map", "3084291314");
        mode1x1.put("AIM_Vertigo", "3309764985");
        mode1x1.put("AIM_Case", "3090032979");
        maps.put("1x1", mode1x1);

        // Для режима 2x2
        Map<String, String> mode2x2 = new HashMap<>();
        mode2x2.put("Vertigo", "de_vertigo");
        mode2x2.put("Inferno", "de_inferno");
        mode2x2.put("Overpass", "de_overpass");
        mode2x2.put("Nuke", "de_nuke");
        mode2x2.put("Dust II", "3347582685");
        mode2x2.put("Mirage", "3347606169");
        mode2x2.put("Train", "3408016560");
        maps.put("2x2", mode2x2);

        // Для режима 5x5
        Map<String, String> mode5x5 = new HashMap<>();
        mode5x5.put("Dust II", "de_dust2");
        mode5x5.put("Inferno", "de_inferno");
        mode5x5.put("Mirage", "de_mirage");
        mode5x5.put("Nuke", "de_nuke");
        mode5x5.put("Ancient", "de_ancient");
        mode5x5.put("Train", "de_train");
        mode5x5.put("Anubis", "de_anubis");
        maps.put("5x5", mode5x5);
    }

    public Map<String, String> getMapsByMode(String mode) {
        return maps.get(mode);
    }
}
