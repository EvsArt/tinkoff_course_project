package edu.java;

import edu.java.configuration.ApiConfig;
import edu.java.configuration.ApplicationConfig;
import edu.java.configuration.BotConfig;
import edu.java.configuration.controller.ControllerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {ApplicationConfig.class, ApiConfig.class, BotConfig.class, ControllerConfig.class})
public class ScrapperApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScrapperApplication.class, args);
    }

}
