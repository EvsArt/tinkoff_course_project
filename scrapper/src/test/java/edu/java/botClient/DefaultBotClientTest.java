package edu.java.botClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.botClient.dto.ApiErrorResponse;
import edu.java.botClient.dto.LinkUpdateRequest;
import edu.java.botClient.dto.PostUpdatesResponse;
import edu.java.configuration.BotConfig;
import edu.java.configuration.retry.RetryConfig;
import edu.java.constants.BotApiPaths;
import edu.java.exceptions.status.BadRequestException;
import edu.java.exceptions.status.ServerErrorException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@WireMockTest(httpPort = 8083)
class DefaultBotClientTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String host = "localhost";
    private static final int port = 8083;

    private static final String urlPath = BotApiPaths.UPDATES;
    private static final ApiErrorResponse testApiErrorResponse = ApiErrorResponse.builder()
        .description("desc").code("400").exceptionMessage("msg")
        .exceptionName("ex").stacktrace(List.of())
        .build();
    private static BotClient client;

    @BeforeAll
    public static void setClient()
        throws URISyntaxException, MalformedURLException {
        client = DefaultBotClient.create(
            new BotConfig(
                new URI(String.format("http://%s:%d", host, port)).toURL(),
                Duration.of(10, ChronoUnit.SECONDS),
                new RetryConfig(0, RetryConfig.Strategy.CONSTANT, List.of(500), Duration.ofMillis(10))
            )
        );
    }

    public void setupOKUpdatesStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .post(WireMock.urlPathEqualTo(urlPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(objectMapper.writer().writeValueAsString(new LinkUpdateRequest()))
                )
        );
    }

    public void setupBadRequestUpdatesStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .post(WireMock.urlPathEqualTo(urlPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.BAD_REQUEST.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(objectMapper.writeValueAsString(testApiErrorResponse))
                )
        );
    }

    private void setupServerErrorUpdatesStub() {
        WireMock.stubFor(
            WireMock
                .post(WireMock.urlPathEqualTo(urlPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    @Test
    void postUpdateShouldReturnOKStatus() throws JsonProcessingException {
        setupOKUpdatesStub();

        LinkUpdateRequest request = LinkUpdateRequest.builder()
            .url("abc")
            .description("desc")
            .tgChatIds(List.of(1L, 2L, 3L))
            .build();

        PostUpdatesResponse realResponse = client.postUpdates(request).block();

        assertThat(realResponse).isEqualTo(new PostUpdatesResponse());
    }

    @Test
    void postUpdateWithWrongArgsShouldReturnBadRequest() throws JsonProcessingException {
        setupBadRequestUpdatesStub();

        LinkUpdateRequest request = LinkUpdateRequest.builder()
            .build();

        Mono<PostUpdatesResponse> realResponseMono = client.postUpdates(request);
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(BadRequestException.class);
    }

    @Test
    void postUpdateWithServerError() throws JsonProcessingException {
        setupServerErrorUpdatesStub();

        LinkUpdateRequest request = LinkUpdateRequest.builder()
            .build();

        Mono<PostUpdatesResponse> realResponseMono = client.postUpdates(request);
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(ServerErrorException.class);
    }

}
