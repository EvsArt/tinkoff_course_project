package edu.java.configuration;

import edu.java.botClient.BotClient;
import edu.java.botClient.DefaultBotClient;
import edu.java.dto.bot.LinkUpdateRequest;
import edu.java.servicesClient.DefaultGitHubClient;
import edu.java.servicesClient.DefaultStackOverflowClient;
import edu.java.servicesClient.GitHubClient;
import edu.java.servicesClient.StackOverflowClient;
import edu.java.botClient.UpdatesQueueProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

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
    @ConditionalOnProperty(value = "app.useQueue", havingValue = "false", matchIfMissing = true)
    public BotClient botClient() {
        return DefaultBotClient.create(botConfig);
    }

    @Bean
    @ConditionalOnProperty(value = "app.useQueue", havingValue = "true")
    public BotClient kafkaBotClient(
        ApplicationConfig applicationConfig,
        KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate
    ) {
        return new UpdatesQueueProducer(applicationConfig.kafkaUpdatesTopic(), kafkaTemplate);
    }

}
