package edu.java.bot.metrics;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UpdatesServiceMetricsAspect {

    private final MetricsContainer metricsContainer;

    public UpdatesServiceMetricsAspect(MetricsContainer metricsContainer) {
        this.metricsContainer = metricsContainer;
    }

    @Pointcut("@annotation(edu.java.bot.metrics.ReceivedHttpUpdate)")
    public void receivedHTTPUpdate() {
    }

    @Around("receivedHTTPUpdate()")
    public Object incrementReceivedHttpUpdates(ProceedingJoinPoint joinPoint) throws Throwable {
        metricsContainer.getReceivedHTTPUpdate().increment();
        return joinPoint.proceed();
    }

    @Pointcut("@annotation(edu.java.bot.metrics.ReceivedKafkaUpdate)")
    public void receivedKafkaUpdate() {
    }

    @Around("receivedKafkaUpdate()")
    public Object incrementReceivedKafkaUpdates(ProceedingJoinPoint joinPoint) throws Throwable {
        metricsContainer.getReceivedKafkaUpdate().increment();
        return joinPoint.proceed();
    }

    @Pointcut("@annotation(edu.java.bot.metrics.ProcessedUpdate)")
    public void processedUpdate() {
    }

    @Around("processedUpdate()")
    public Object incrementSentUpdates(ProceedingJoinPoint joinPoint) throws Throwable {
        metricsContainer.getSentUpdates().increment();
        return joinPoint.proceed();
    }

    @Pointcut("@annotation(edu.java.bot.metrics.ErrorUpdate)")
    public void errorUpdate() {
    }

    @Around("errorUpdate()")
    public Object incrementErrorUpdates(ProceedingJoinPoint joinPoint) throws Throwable {
        metricsContainer.getErrorUpdates().increment();
        return joinPoint.proceed();
    }

    @Pointcut("@annotation(edu.java.bot.metrics.ReceivedTgMessage)")
    public void receivedTgMessage() {
    }

    @Around("receivedTgMessage()")
    public Object incrementReceivedTgMessages(ProceedingJoinPoint joinPoint) throws Throwable {
        metricsContainer.getReceivedTgMessages().increment();
        return joinPoint.proceed();
    }

}
