package kr.kdev.demo.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "spring.main", ignoreUnknownFields = false)
public class SpringMainProperties {
    private String bannerMode;
}
