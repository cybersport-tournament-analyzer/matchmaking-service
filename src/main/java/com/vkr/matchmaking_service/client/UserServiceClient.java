package com.vkr.matchmaking_service.client;

import com.vkr.matchmaking_service.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8080/")
public interface UserServiceClient {
    @GetMapping("/users/steam/{steamId}")
    UserDto getUserBySteamId(@PathVariable String steamId);

}
