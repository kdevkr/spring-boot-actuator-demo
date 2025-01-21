package kr.kdev.demo;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TestEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            // NOTE: Set application properties from .env file
            Properties properties = new Properties();
            properties.load(new ClassPathResource(".env.properties").getInputStream());
            String filename = properties.getProperty("filename", ".env");
            Dotenv dotenv = new DotenvBuilder().filename(filename).ignoreIfMalformed().ignoreIfMissing().load();
            Map<String, String> props = new HashMap<>();
            System.out.printf("[Set application properties from %s]%n", filename);
            dotenv.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE).forEach(entry -> {
                String key = entry.getKey().toLowerCase().replace("_", ".");
                System.out.printf("\tSet property: %s=%s%n", key, entry.getValue());
                props.put(key, entry.getValue());
            });
            TestPropertyValues.of(props).applyTo(environment);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOrder() {
        return ConfigDataEnvironmentPostProcessor.ORDER - 1;
    }
}
