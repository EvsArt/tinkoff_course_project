package edu.java.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.configuration.ApiConfig;
import edu.java.constants.GitHubApiPaths;
import edu.java.dto.GitHubRepoRequest;
import edu.java.dto.GitHubRepoResponse;
import edu.java.exceptions.status.ForbiddenException;
import edu.java.exceptions.status.MovedPermanentlyException;
import edu.java.exceptions.status.ResourceNotFoundException;
import edu.java.exceptions.status.ServerErrorException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMapAdapter;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@WireMockTest(httpPort = 8082)
class DefaultGitHubClientTest {

    private static final String host = "localhost";
    private static final int port = 8082;
    private final static GitHubRepoResponse expResponse = new GitHubRepoResponse(
        1L,
        "Name/Repo",
        OffsetDateTime.parse("2007-12-03T10:15:30Z"),
        OffsetDateTime.parse("2007-12-05T11:14:31Z")
    );
    private static final String responseJson = """
        {
        "id": 1,
        "full_name": "Name/Repo",
        "updated_at": "2007-12-03T10:15:30Z",
        "pushed_at": "2007-12-05T11:14:31Z"
        }
        """;
    private static final String name = "Name";
    private static final String repo = "Repo";
    private static final String urlPath = GitHubApiPaths.GET_REPOSITORY
        .replaceAll("\\{" + GitHubApiPaths.OWNER_NAME_PARAM + "}", name)
        .replaceAll("\\{" + GitHubApiPaths.REPO_NAME_PARAM + "}", repo);
    private static DefaultGitHubClient client;

    @BeforeAll
    public static void setClientAndConvertResponse()
        throws URISyntaxException, MalformedURLException {
        client = DefaultGitHubClient.create(
            new ApiConfig.GitHubConfig(
                new URI(String.format("http://%s:%d", host, port)).toURL(),
                new MultiValueMapAdapter<>(new HashMap<>()),
                Duration.of(10, ChronoUnit.SECONDS)
            )
        );
    }

    public void setupOKGetRepositoryStub() {
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

    public void setupNotFoundGetRepositoryStub() {
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

    private void setupForbiddenGetRepositoryStub() {
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

    private void setupMovedPermanentlyGetRepositoryStub() {
        WireMock.stubFor(
            WireMock
                .get(WireMock.urlPathEqualTo(urlPath))
                .withPort(port)
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.aResponse()
                    .withStatus(HttpStatus.MOVED_PERMANENTLY.value())
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                )
        );
    }

    private void setupServerErrorGetRepositoryStub() {
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
    void getRepositoryWithOKStatus() {
        setupOKGetRepositoryStub();

        GitHubRepoResponse realResponse =
            client.getRepository(new GitHubRepoRequest(name, repo)).block();

        assertThat(realResponse).isEqualTo(expResponse);
    }

    @Test
    void getRepositoryWithNotFoundStatus() {
        setupNotFoundGetRepositoryStub();

        Mono<GitHubRepoResponse> realResponseMono =
            client.getRepository(new GitHubRepoRequest(name, repo));
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getRepositoryWithForbiddenStatus() {
        setupForbiddenGetRepositoryStub();

        Mono<GitHubRepoResponse> realResponseMono =
            client.getRepository(new GitHubRepoRequest(name, repo));
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getRepositoryWithMovedPermanentlyStatus() {
        setupMovedPermanentlyGetRepositoryStub();

        Mono<GitHubRepoResponse> realResponseMono =
            client.getRepository(new GitHubRepoRequest(name, repo));
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(MovedPermanentlyException.class);
    }

    @Test
    void getRepositoryWithServerErrorStatus() {
        setupServerErrorGetRepositoryStub();

        Mono<GitHubRepoResponse> realResponseMono =
            client.getRepository(new GitHubRepoRequest(name, repo));
        Throwable thrown = catchThrowable(realResponseMono::block);

        assertThat(thrown).isInstanceOf(ServerErrorException.class);
    }

}
