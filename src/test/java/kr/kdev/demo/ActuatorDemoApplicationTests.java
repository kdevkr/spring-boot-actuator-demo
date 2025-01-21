package kr.kdev.demo;

import kr.kdev.demo.properties.SpringMainProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class ActuatorDemoApplicationTests {
    @Value("${spring.main.banner-mode:console}")
    String bannerMode;

    @Autowired
    SpringMainProperties springMainProperties;

    @Test
    void contextLoads() {
        Assertions.assertEquals("off", bannerMode);
        Assertions.assertEquals(bannerMode, springMainProperties.getBannerMode());
    }

}
