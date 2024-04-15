package edu.java.metrics;

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

    @Pointcut("@annotation(SentHttpUpdate)")
    public void sentHTTPUpdate() {
    }

    @Around("sentHTTPUpdate()")
    public Object incrementSentHttpUpdates(ProceedingJoinPoint joinPoint) throws Throwable {
        metricsContainer.getSentHTTPUpdate().increment();
        return joinPoint.proceed();
    }

    @Pointcut("@annotation(SentKafkaUpdate)")
    public void sentKafkaUpdate() {
    }

    @Around("sentKafkaUpdate()")
    public Object incrementSentKafkaUpdates(ProceedingJoinPoint joinPoint) throws Throwable {
        metricsContainer.getSentKafkaUpdate().increment();
        return joinPoint.proceed();
    }

}
