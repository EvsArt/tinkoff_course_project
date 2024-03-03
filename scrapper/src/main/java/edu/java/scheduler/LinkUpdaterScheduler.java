package edu.java.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@EnableScheduling
@Configuration
@ConditionalOnExpression("${app.scheduler.enable}")
public class LinkUpdaterScheduler {

    @Scheduled(fixedDelayString = "PT" + "${app.scheduler.interval}")
    public void update() {
        log.info("Scheduler is working!!!");
    }

}
