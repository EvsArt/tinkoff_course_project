package edu.java.bot;

import edu.java.bot.api.configuration.controller.ControllerConfig;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.scrapperClient.config.ScrapperConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationConfig.class, ScrapperConfig.class, ControllerConfig.class})
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
