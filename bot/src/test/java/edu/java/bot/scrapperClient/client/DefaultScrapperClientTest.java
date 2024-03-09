package edu.java.bot.scrapperClient.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.api.dto.ApiErrorResponse;
import edu.java.bot.constants.ScrapperApiPaths;
import edu.java.bot.scrapperClient.config.ScrapperConfig;
import edu.java.bot.scrapperClient.dto.AddLinkRequest;
import edu.java.bot.scrapperClient.dto.LinkResponse;
import edu.java.bot.scrapperClient.dto.ListLinksResponse;
import edu.java.bot.scrapperClient.dto.RegisterChatResponse;
import edu.java.bot.scrapperClient.dto.RemoveLinkRequest;
import edu.java.bot.scrapperClient.exceptions.status.BadRequestException;
import edu.java.bot.scrapperClient.exceptions.status.ResourceNotFoundException;
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
class DefaultScrapperClientTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String host = "localhost";
    private static final int port = 8083;

    private static final String tgChatIdHeader = "Tg-Chat-Id";
    private static final long tgChatId = 1L;
    private static final String urlChatPath = ScrapperApiPaths.CHAT
        .replaceAll("\\{" + ScrapperApiPaths.ID_PARAM + "}", String.valueOf(tgChatId));
    private static final String urlLinksPath = ScrapperApiPaths.LINKS;

    private static final ApiErrorResponse testApiErrorResponse = ApiErrorResponse.builder()
        .description("desc").code("400").exceptionMessage("msg")
        .exceptionName("ex").stacktrace(List.of())
        .build();

    private static final LinkResponse testLinkResponse = new LinkResponse(1L, URI.create("url"));

    private static ScrapperClient client;
    private static final ListLinksResponse links = new ListLinksResponse(List.of(
        new LinkResponse(1L, URI.create("a")),
        new LinkResponse(2L, URI.create("b")),
        new LinkResponse(3L, URI.create("c"))
    ));

    @BeforeAll
    public static void setClient()
        throws URISyntaxException, MalformedURLException {
        client = DefaultScrapperClient.create(
            new ScrapperConfig(
                new URI(String.format("http://%s:%d", host, port)).toURL(),
                Duration.of(10, ChronoUnit.SECONDS)
            )
        );
    }

    public void setupOKPostChatStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .post(WireMock.urlPathEqualTo(urlChatPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(new RegisterChatResponse()))
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    public void setupBadRequestPostChatStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .post(WireMock.urlPathEqualTo(urlChatPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.BAD_REQUEST.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .withBody(objectMapper.writeValueAsString(testApiErrorResponse))
                )
        );
    }

    public void setupOKGetLinksStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .get(WireMock.urlPathEqualTo(urlLinksPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(tgChatIdHeader, WireMock.equalTo(String.valueOf(tgChatId)))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(links))
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    public void setupBadRequestGetLinksStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .get(WireMock.urlPathEqualTo(urlLinksPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(tgChatIdHeader, WireMock.equalTo(String.valueOf(tgChatId)))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.BAD_REQUEST.value())
                    .withBody(objectMapper.writeValueAsString(testApiErrorResponse))
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    public void setupNotFoundGetLinksStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .get(WireMock.urlPathEqualTo(urlLinksPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(tgChatIdHeader, WireMock.equalTo(String.valueOf(tgChatId)))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.NOT_FOUND.value())
                    .withBody(objectMapper.writeValueAsString(testApiErrorResponse))
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    public void setupOKPostLinksStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .post(WireMock.urlPathEqualTo(urlLinksPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(tgChatIdHeader, WireMock.equalTo(String.valueOf(tgChatId)))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(testLinkResponse))
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    public void setupBadRequestPostLinksStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .post(WireMock.urlPathEqualTo(urlLinksPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(tgChatIdHeader, WireMock.equalTo(String.valueOf(tgChatId)))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.BAD_REQUEST.value())
                    .withBody(objectMapper.writeValueAsString(testApiErrorResponse))
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    public void setupNotFoundPostLinksStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .post(WireMock.urlPathEqualTo(urlLinksPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(tgChatIdHeader, WireMock.equalTo(String.valueOf(tgChatId)))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.NOT_FOUND.value())
                    .withBody(objectMapper.writeValueAsString(testApiErrorResponse))
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    public void setupOKDeleteLinksStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .delete(WireMock.urlPathEqualTo(urlLinksPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(tgChatIdHeader, WireMock.equalTo(String.valueOf(tgChatId)))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withBody(objectMapper.writeValueAsString(testLinkResponse))
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    public void setupBadRequestDeleteLinksStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .delete(WireMock.urlPathEqualTo(urlLinksPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(tgChatIdHeader, WireMock.equalTo(String.valueOf(tgChatId)))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.BAD_REQUEST.value())
                    .withBody(objectMapper.writeValueAsString(testApiErrorResponse))
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    public void setupNotFoundDeleteLinksStub() throws JsonProcessingException {
        WireMock.stubFor(
            WireMock
                .delete(WireMock.urlPathEqualTo(urlLinksPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(tgChatIdHeader, WireMock.equalTo(String.valueOf(tgChatId)))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.NOT_FOUND.value())
                    .withBody(objectMapper.writeValueAsString(testApiErrorResponse))
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    @Test
    void getLinksWithOKStatus() throws JsonProcessingException {
        setupOKGetLinksStub();

        ListLinksResponse realResponse = client.getLinks(tgChatId).block();

        assertThat(realResponse).isEqualTo(links);
    }

    @Test
    void getLinksWithBadRequestStatus() throws JsonProcessingException {
        setupBadRequestGetLinksStub();

        Mono<ListLinksResponse> realResponseMono = client.getLinks(tgChatId);
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(BadRequestException.class);
    }

    @Test
    void getLinksWithNotFoundStatus() throws JsonProcessingException {
        setupNotFoundGetLinksStub();

        Mono<ListLinksResponse> realResponseMono = client.getLinks(tgChatId);
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void postLinksWithOKStatus() throws JsonProcessingException {
        setupOKPostLinksStub();

        LinkResponse realResponse = client.addLink(tgChatId, new AddLinkRequest()).block();

        assertThat(realResponse).isEqualTo(testLinkResponse);
    }

    @Test
    void postLinksWithBadRequestStatus() throws JsonProcessingException {
        setupBadRequestPostLinksStub();

        Mono<LinkResponse> realResponseMono = client.addLink(tgChatId, new AddLinkRequest());
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(BadRequestException.class);
    }

    @Test
    void postLinksWithNotFoundStatus() throws JsonProcessingException {
        setupNotFoundPostLinksStub();

        Mono<LinkResponse> realResponseMono = client.addLink(tgChatId, new AddLinkRequest());
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteLinksWithOKStatus() throws JsonProcessingException {
        setupOKDeleteLinksStub();

        LinkResponse realResponse = client.removeLink(tgChatId, new RemoveLinkRequest()).block();

        assertThat(realResponse).isEqualTo(testLinkResponse);
    }

    @Test
    void deleteLinksWithBadRequestStatus() throws JsonProcessingException {
        setupBadRequestDeleteLinksStub();

        Mono<LinkResponse> realResponseMono = client.removeLink(tgChatId, new RemoveLinkRequest());
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(BadRequestException.class);
    }

    @Test
    void deleteLinksWithNotFoundStatus() throws JsonProcessingException {
        setupNotFoundDeleteLinksStub();

        Mono<LinkResponse> realResponseMono = client.removeLink(tgChatId, new RemoveLinkRequest());
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(ResourceNotFoundException.class);
    }

}
