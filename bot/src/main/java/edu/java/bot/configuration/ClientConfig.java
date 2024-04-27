package edu.java.bot.configuration;

import edu.java.bot.scrapperClient.client.DefaultScrapperClient;
import edu.java.bot.scrapperClient.client.ScrapperClient;
import edu.java.bot.configuration.ScrapperConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    private final ScrapperConfig config;

    @Autowired
    public ClientConfig(ScrapperConfig config) {
        this.config = config;
    }

    @Bean
    public ScrapperClient scrapperClient() {
        return DefaultScrapperClient.create(config);
    }

}
