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
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ConstantRetryTest {

    static String expectedResponse = "expect that it works";
    static MockWebServer mockWebServer = new MockWebServer();
    HttpUrl url = mockWebServer.url("/mvuri1");
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
        return new RetryConfig(n, RetryConfig.Strategy.CONSTANT, List.of(500), Duration.ofSeconds(1));
    }

    @Test
    public void testClientDoRetry() {
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
    public void testClientThrowErrorWhenRetry() throws InterruptedException {
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

    // 1s * 3 = 3s
    @Test
    public void testClient3RetriesWith1SDelay_shouldRunsAbout3Seconds() throws IOException {
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

        long expTime = retryConfig.getInterval().multipliedBy(retryConfig.getMaxAttempts()).toMillis();

        assertThat(time).isCloseTo(expTime, Percentage.withPercentage(30));
    }

    // 1s * 4 = 4s
    @Test
    public void testClient4RetriesWith1SDelay_shouldRunsAbout4Seconds() throws IOException {
        int retries = 4;
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

        long expTime = retryConfig.getInterval().multipliedBy(retryConfig.getMaxAttempts()).toMillis();

        assertThat(time).isCloseTo(expTime, Percentage.withPercentage(30));
    }

}
