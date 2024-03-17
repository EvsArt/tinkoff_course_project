package edu.java.configuration;

import edu.java.botClient.BotClient;
import edu.java.botClient.DefaultBotClient;
import edu.java.botClient.config.BotConfig;
import edu.java.client.DefaultGitHubClient;
import edu.java.client.DefaultStackOverflowClient;
import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {

    private final ApiConfig apiConfig;
    private final BotConfig botConfig;

    @Autowired
    public ClientConfiguration(ApiConfig apiConfig, BotConfig botConfig) {
        this.apiConfig = apiConfig;
        this.botConfig = botConfig;
    }

    @Bean
    public GitHubClient gitHubClient() {
        return DefaultGitHubClient.create(apiConfig.gitHub());
    }

    @Bean
    public StackOverflowClient stackOverflowClient() {
        return DefaultStackOverflowClient.create(apiConfig.stackOverflow());
    }

    @Bean
    public BotClient botClient() {
        return DefaultBotClient.create(botConfig);
    }

}
