package edu.java.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.configuration.ApiConfig;
import edu.java.constants.StackOverflowApiPaths;
import edu.java.dto.StackOverflowQuestionResponse;
import edu.java.exceptions.status.BadRequestException;
import edu.java.exceptions.status.ForbiddenException;
import edu.java.exceptions.status.MovedPermanentlyException;
import edu.java.exceptions.status.ResourceNotFoundException;
import edu.java.exceptions.status.ServerErrorException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMapAdapter;
import reactor.core.publisher.Mono;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@WireMockTest(httpPort = 8082)
class DefaultStackOverflowClientTest {

    private static final String host = "localhost";
    private static final int port = 8082;
    private static DefaultStackOverflowClient client;
    private final static StackOverflowQuestionResponse expResponse =
        new StackOverflowQuestionResponse(
            1L,
            "MyQuestion",
            OffsetDateTime.of(LocalDateTime.ofEpochSecond(1708557503, 0, ZoneOffset.UTC), ZoneOffset.UTC)
        );

    private static final String responseJson = """
        {"items":[{
            "question_id": 1,
            "title": "MyQuestion",
            "last_activity_date": 1708557503
        }]}
        """;
    private static final Long id = 1L;
    private static final String urlPath = StackOverflowApiPaths.GET_QUESTION
        .replaceAll("\\{" + StackOverflowApiPaths.QUESTION_ID_PARAM + "}", String.valueOf(id));

    @BeforeAll
    public static void setClientAndConvertResponse()
        throws URISyntaxException, MalformedURLException {
        client = DefaultStackOverflowClient.create(
            new ApiConfig.StackOverflowConfig(
                new URI(String.format("http://%s:%d", host, port)).toURL(),
                new MultiValueMapAdapter<>(new HashMap<>()),
                Duration.of(10, ChronoUnit.SECONDS)
            )
        );
    }

    public void setupOKGetQuestionStub() {
        WireMock.stubFor(
            WireMock
                .get(WireMock.urlPathEqualTo(urlPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody(responseJson)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    public void setupNotFoundGetQuestionStub() {
        WireMock.stubFor(
            WireMock
                .get(WireMock.urlPathEqualTo(urlPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.NOT_FOUND.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    private void setupForbiddenGetQuestionStub() {
        WireMock.stubFor(
            WireMock
                .get(WireMock.urlPathEqualTo(urlPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.FORBIDDEN.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    private void setupBadRequestGetQuestionStub() {
        WireMock.stubFor(
            WireMock
                .get(WireMock.urlPathEqualTo(urlPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.BAD_REQUEST.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    private void setupServerErrorGetQuestionStub() {
        WireMock.stubFor(
            WireMock
                .get(WireMock.urlPathEqualTo(urlPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    @Test
    void getQuestionWithOKStatus() {
        setupOKGetQuestionStub();

        StackOverflowQuestionResponse realResponse = client.getQuestionById(id).block();

        assertThat(realResponse).isEqualTo(expResponse);
    }

    @Test
    void getQuestionWithNotFoundStatus() {
        setupNotFoundGetQuestionStub();

        Mono<StackOverflowQuestionResponse> realResponseMono = client.getQuestionById(id);
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getQuestionWithForbiddenStatus() {
        setupForbiddenGetQuestionStub();

        Mono<StackOverflowQuestionResponse> realResponseMono = client.getQuestionById(id);
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getQuestionWithBadRequestStatus() {
        setupBadRequestGetQuestionStub();

        Mono<StackOverflowQuestionResponse> realResponseMono = client.getQuestionById(id);
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(BadRequestException.class);
    }

    @Test
    void getQuestionWithServerErrorStatus() {
        setupServerErrorGetQuestionStub();

        Mono<StackOverflowQuestionResponse> realResponseMono = client.getQuestionById(id);
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(ServerErrorException.class);
    }

}
