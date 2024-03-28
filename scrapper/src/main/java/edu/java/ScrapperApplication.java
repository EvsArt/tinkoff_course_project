package edu.java;

import edu.java.configuration.BotConfig;
import edu.java.configuration.ApiConfig;
import edu.java.configuration.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(value = {ApplicationConfig.class, ApiConfig.class, BotConfig.class})
public class ScrapperApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScrapperApplication.class, args);
    }

    @Bean
    public String test(ApplicationContext context) {
        System.out.println(context.getBean(ApiConfig.class));
        return "";
    }

}
