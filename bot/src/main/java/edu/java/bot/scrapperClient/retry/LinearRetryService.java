package edu.java.bot.scrapperClient.retry;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@UtilityClass
public class LinearRetryService {

    static final BiFunction<Long, Retry.RetrySignal, Throwable> EXCEPTION_GENERATOR = (maxAttempts, rs) ->
        Exceptions.retryExhausted("Retries exhausted: " + (
                rs.totalRetries() + "/" + maxAttempts
            ), rs.failure()
        );

    public static @NotNull Retry createRetry(
        long maxAttempts,
        Duration delay,
        @NotNull Predicate<Throwable> filter
    ) {
        final Duration minBackoff = delay;
        return Retry.from(
            t -> Flux.deferContextual(cv ->
                t.contextWrite(cv)
                    .concatMap(retryWhenState -> {
                        //capture the state immediately
                        Retry.RetrySignal copy = retryWhenState.copy();
                        Throwable currentFailure = copy.failure();
                        long iteration = copy.totalRetries();

                        if (currentFailure == null) {
                            return Mono.error(
                                new IllegalStateException("Retry.RetrySignal#failure() not expected to be null")
                            );
                        }

                        if (!filter.test(currentFailure)) {
                            return Mono.error(currentFailure);
                        }

                        if (iteration >= maxAttempts) {
                            return Mono.error(EXCEPTION_GENERATOR.apply(maxAttempts, copy));
                        }

                        // (minBackoff = 3s) -> {3s, 6s, 9s, 12s, ...}
                        Duration nextBackoff = minBackoff.multipliedBy(iteration + 1);
                        // +1 needs for delay before 1 retry
                        return Mono.delay(nextBackoff, Schedulers.parallel());
                    })
            )
        );
    }

}
