package edu.java.bot.metrics;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Aspect
@Configuration
@EnableAspectJAutoProxy
public class UpdatesServiceMetricsAspect {

    private final MetricsContainer metricsContainer;

    public UpdatesServiceMetricsAspect(MetricsContainer metricsContainer) {
        this.metricsContainer = metricsContainer;
    }

    @Pointcut("@annotation(edu.java.bot.metrics.ReceivedHttpUpdate)")
    public void receivedHTTPUpdate() {
    }

    @After("receivedHTTPUpdate()")
    public void incrementReceivedHttpUpdates() {
        metricsContainer.getReceivedHTTPUpdate().increment();
    }

    @Pointcut("@annotation(edu.java.bot.metrics.ReceivedKafkaUpdate)")
    public void receivedKafkaUpdate() {
    }

    @After("receivedKafkaUpdate()")
    public void incrementReceivedKafkaUpdates() {
        metricsContainer.getReceivedKafkaUpdate().increment();
    }

    @Pointcut("@annotation(edu.java.bot.metrics.ProcessedUpdate)")
    public void processedUpdate() {
    }

    @After("processedUpdate()")
    public void incrementSentUpdates() {
        metricsContainer.getSentUpdates().increment();
    }

    @Pointcut("@annotation(edu.java.bot.metrics.ErrorUpdate)")
    public void errorUpdate() {
    }

    @After("errorUpdate()")
    public void incrementErrorUpdates() {
        metricsContainer.getErrorUpdates().increment();
    }

    @Pointcut("@annotation(edu.java.bot.metrics.ReceivedTgMessage)")
    public void receivedTgMessage() {
    }

    @After("receivedTgMessage()")
    public void incrementReceivedTgMessages() {
        metricsContainer.getReceivedTgMessages().increment();
    }

}
