package kr.kdev.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ActuatorDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActuatorDemoApplication.class, args);
    }
    
}
