package kr.kdev.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @Autowired(required = false)
    public HealthEndpoint healthEndpoint;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        // NOTE: https://toss.tech/article/how-to-work-health-check-in-spring-boot-actuator
        if (healthEndpoint != null) {
            HealthComponent ping = healthEndpoint.healthForPath("ping");
            if (!Status.UP.equals(ping.getStatus())) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ping.getStatus().getCode());
            }
            return ResponseEntity.ok(ping.getStatus().getCode());
        }
        return ResponseEntity.ok(HttpStatus.OK.name());
    }
}
