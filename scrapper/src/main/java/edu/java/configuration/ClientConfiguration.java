package edu.java.configuration;

import edu.java.botClient.BotClient;
import edu.java.botClient.DefaultBotClient;
import edu.java.client.DefaultGitHubClient;
import edu.java.client.DefaultStackOverflowClient;
import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import edu.java.kafkaBotClient.UpdatesQueueProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    @ConditionalOnProperty(value = "${app.useQueue}", havingValue = "false")
    public BotClient botClient() {
        return DefaultBotClient.create(botConfig);
    }

    @Bean
    @ConditionalOnProperty(value = "${app.useQueue}", havingValue = "true")
    public BotClient kafkaBotClient(UpdatesQueueProducer producer) {
        return producer;
    }

}
