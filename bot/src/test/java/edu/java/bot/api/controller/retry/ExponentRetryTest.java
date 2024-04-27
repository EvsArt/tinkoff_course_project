package edu.java.bot.api.controller.retry;

import edu.java.bot.scrapperClient.retry.RetryConfig;
import edu.java.bot.exceptions.status.ServerErrorException;
import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.QueueDispatcher;
import okhttp3.mockwebserver.RecordedRequest;
import okio.Buffer;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Rollback
public class ExponentRetryTest {

    static String expectedResponse = "expect that it works";
    static MockWebServer mockWebServer = new MockWebServer();
    HttpUrl url = mockWebServer.url("/mvuri3");
    WebClient webClient = WebClient.create();

    private static void clearServerResponse() throws InterruptedException {
        QueueDispatcher d = (QueueDispatcher) mockWebServer.getDispatcher();
        d.dispatch(new RecordedRequest("", Headers.of(), List.of(), 1, new Buffer(), 1, new Socket(), null));
    }

    private void setResponsesWithNErrors(int n) {
        IntStream.range(0, n).forEach(
            i -> mockWebServer.enqueue(new MockResponse().setResponseCode(500))
        );
        mockWebServer.enqueue(new MockResponse().setResponseCode(200)
            .setBody(expectedResponse));
    }

    private RetryConfig getRetryConfigWithNRetries(int n) {
        return new RetryConfig(n, RetryConfig.Strategy.EXPONENTIAL, List.of(500), Duration.ofMillis(500));
    }

    @Test
    public void testClientDoRetry() throws IOException, InterruptedException {
        setResponsesWithNErrors(2);
        RetryConfig retryConfig = getRetryConfigWithNRetries(2);

        Mono<String> responseMono = webClient.post()
            .uri(url.uri())
            .body(BodyInserters.fromObject("myRequest"))
            .retrieve()
            .onStatus(
                httpStatus -> httpStatus.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                response -> Mono.error(new ServerErrorException())
            )
            .bodyToMono(String.class)
            .retryWhen(retryConfig.toReactorRetry());

        StepVerifier.create(responseMono)
            .expectNext(expectedResponse)
            .expectComplete().verify();
    }

    @Test
    public void testClientThrowErrorWhenRetry() throws IOException, InterruptedException {
        setResponsesWithNErrors(3);
        RetryConfig retryConfig = getRetryConfigWithNRetries(2);

        Mono<String> responseMono = webClient.post()
            .uri(url.uri())
            .body(BodyInserters.fromObject("myRequest"))
            .retrieve()
            .onStatus(
                httpStatus -> httpStatus.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                response -> Mono.error(new ServerErrorException())
            )
            .bodyToMono(String.class)
            .retryWhen(retryConfig.toReactorRetry())
            .onErrorMap(it -> (Exceptions.isRetryExhausted(it)) ? it.getCause() : it);

        StepVerifier.create(responseMono)
            .expectErrorMatches(it -> it instanceof ServerErrorException)
            .verify();

        clearServerResponse();
    }

    // 2s + ~4s + ~8s = ~14s
    @Test
    public void testClient3RetriesWith2SDelay_shouldRunsAbout14Seconds() throws IOException, InterruptedException {
        int retries = 3;
        setResponsesWithNErrors(retries);
        RetryConfig retryConfig = getRetryConfigWithNRetries(retries);

        Mono<String> responseMono = webClient.post()
            .uri(url.uri())
            .body(BodyInserters.fromObject("myRequest"))
            .retrieve()
            .onStatus(
                httpStatus -> httpStatus.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                response -> Mono.error(new ServerErrorException())
            )
            .bodyToMono(String.class)
            .retryWhen(retryConfig.toReactorRetry());

        long time = System.currentTimeMillis();
        StepVerifier.create(responseMono)
            .expectNext(expectedResponse)
            .expectComplete()
            .verify();
        time = System.currentTimeMillis() - time - 200;     // ~200ms is not requests time

        // geometric progression
        long expTime = retryConfig.getInterval().multipliedBy((long) Math.pow(2, retries) - 1).toMillis();

        assertThat(time).isCloseTo(expTime, Percentage.withPercentage(retryConfig.getJitter() * 100));
    }

    // 2s + ~4s = ~6s
    @Test
    public void testClient2RetriesWith2SDelay_shouldRunsAbout6Seconds() throws IOException, InterruptedException {
        int retries = 2;
        setResponsesWithNErrors(retries);
        RetryConfig retryConfig = getRetryConfigWithNRetries(retries);

        Mono<String> responseMono = webClient.post()
            .uri(url.uri())
            .body(BodyInserters.fromObject("myRequest"))
            .retrieve()
            .onStatus(
                httpStatus -> httpStatus.equals(HttpStatus.INTERNAL_SERVER_ERROR),
                response -> Mono.error(new ServerErrorException())
            )
            .bodyToMono(String.class)
            .retryWhen(retryConfig.toReactorRetry());

        long time = System.currentTimeMillis();
        StepVerifier.create(responseMono)
            .expectNext(expectedResponse)
            .expectComplete()
            .verify();
        time = System.currentTimeMillis() - time - 200;     // ~200ms is not requests time

        // geometric progression
        long expTime = retryConfig.getInterval().multipliedBy((long) Math.pow(2, retries) - 1).toMillis();

        assertThat(time).isCloseTo(expTime, Percentage.withPercentage(retryConfig.getJitter() * 100));
    }

}
