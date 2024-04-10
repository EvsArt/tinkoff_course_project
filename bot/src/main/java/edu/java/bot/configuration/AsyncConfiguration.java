package edu.java.bot.configuration;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration implements AsyncConfigurer {

    private final ApplicationConfig.Async asyncConfig;

    public AsyncConfiguration(ApplicationConfig applicationConfig) {
        this.asyncConfig = applicationConfig.async();
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncConfig.corePoolSize());
        executor.setMaxPoolSize(asyncConfig.maxPoolSize());
        executor.setQueueCapacity(asyncConfig.queueCapacity());
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }

}
