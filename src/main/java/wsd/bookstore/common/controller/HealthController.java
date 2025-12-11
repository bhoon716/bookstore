package wsd.bookstore.common.controller;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final BuildProperties buildProperties;

    @GetMapping("/health")
    public Map<String, Object> checkHealth() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("version", buildProperties.getVersion());
        health.put("buildTime", buildProperties.getTime());
        health.put("timestamp", LocalDateTime.now());
        return health;
    }
}
