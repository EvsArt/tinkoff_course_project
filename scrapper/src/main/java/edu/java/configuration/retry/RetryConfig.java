package edu.java.configuration.retry;

import edu.java.exceptions.status.StatusException;
import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.ToString;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;
import reactor.util.retry.RetrySpec;

@ToString
public final class RetryConfig {
    @Getter private final int maxAttempts;
    private final Strategy strategy;
    @Getter private final Duration interval;
    @Getter private final double jitter = 0.5;
    private final Predicate<Throwable> statusFilterPredicate;

    public RetryConfig(int maxAttempts, Strategy strategy, List<Integer> codes, Duration interval) {
        this.maxAttempts = maxAttempts;
        this.strategy = strategy;
        this.interval = interval;
        statusFilterPredicate =
            ex -> !(ex instanceof StatusException)
                || codes.contains(((StatusException) ex).getStatus().value());
    }

    public Retry toReactorRetry() {
        return switch (strategy) {
            case CONSTANT -> RetrySpec.fixedDelay(maxAttempts, interval)
                .filter(statusFilterPredicate);
            case LINEAR -> LinearRetryService.createRetry(
                maxAttempts, interval, statusFilterPredicate
            );
            case EXPONENTIAL -> RetryBackoffSpec.backoff(maxAttempts, interval)
                .jitter(jitter)
                .filter(statusFilterPredicate);
        };

    }

    public enum Strategy {
        CONSTANT,
        LINEAR,
        EXPONENTIAL
    }
}
