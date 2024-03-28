package edu.java.configuration.model;

import edu.java.exceptions.status.StatusException;
import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import lombok.ToString;
import reactor.util.retry.RetryBackoffSpec;
import reactor.util.retry.RetrySpec;

@ToString
public final class Retry {
    private final int maxAttempts;
    private final Strategy strategy;
    private final Duration interval;
    private final Predicate<Throwable> statusFilterPredicate;

    public Retry(int maxAttempts, Strategy strategy, List<Integer> codes, Duration interval) {
        this.maxAttempts = maxAttempts;
        this.strategy = strategy;
        this.interval = interval;
        statusFilterPredicate =
            ex -> !(ex instanceof StatusException) ||
                codes.contains(((StatusException) ex).getStatus().value());
    }

    public reactor.util.retry.Retry toReactorRetry() {
        return switch (strategy) {
            case CONSTANT -> RetrySpec.max(maxAttempts)
                .filter(statusFilterPredicate);
            case LINEAR -> RetryBackoffSpec.fixedDelay(maxAttempts, interval)
                .filter(statusFilterPredicate);
            case EXPONENTIAL -> RetryBackoffSpec.backoff(maxAttempts, interval)
                .filter(statusFilterPredicate);
        };

    }

    public enum Strategy {
        CONSTANT,
        LINEAR,
        EXPONENTIAL
    }
}
