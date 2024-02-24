package edu.java.configuration;

import edu.java.client.DefaultGitHubClient;
import edu.java.client.DefaultStackOverflowClient;
import edu.java.client.AsyncGitHubClient;
import edu.java.client.StackOverflowClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class ClientConfiguration {

    private final ApiConfig apiConfig;

    @Autowired
    public ClientConfiguration(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    @Bean
    public AsyncGitHubClient gitHubClient() {
        return DefaultGitHubClient.create(apiConfig.gitHub());
    }

    @Bean
    public StackOverflowClient stackOverflowClient() {
        return DefaultStackOverflowClient.create(apiConfig.stackOverflow());
    }

}
