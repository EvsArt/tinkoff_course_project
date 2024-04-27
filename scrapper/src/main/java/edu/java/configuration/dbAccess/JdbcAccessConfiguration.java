package edu.java.configuration.dbAccess;

import edu.java.servicesClient.GitHubClient;
import edu.java.servicesClient.StackOverflowClient;
import edu.java.domain.jdbcRepository.JdbcGitHubLinkInfoRepository;
import edu.java.domain.jdbcRepository.JdbcLinkRepository;
import edu.java.domain.jdbcRepository.JdbcStackOverFlowLinkInfoRepository;
import edu.java.domain.jdbcRepository.JdbcTgChatRepository;
import edu.java.service.GitHubLinkInfoService;
import edu.java.service.LinkInfoService;
import edu.java.service.LinkService;
import edu.java.service.LinksParsingService;
import edu.java.service.StackOverFlowLinkInfoService;
import edu.java.service.TgChatService;
import edu.java.service.jdbc.JdbcGitHubLinkInfoService;
import edu.java.service.jdbc.JdbcLinkService;
import edu.java.service.jdbc.JdbcStackOverFlowLinkInfoService;
import edu.java.service.jdbc.JdbcTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {

    @Bean
    public GitHubLinkInfoService gitHubLinkInfoService(
        GitHubClient gitHubClient,
        LinksParsingService linksParsingService,
        JdbcGitHubLinkInfoRepository jdbcGitHubLinkInfoRepository,
        JdbcLinkRepository jdbcLinkRepository
    ) {
        return new JdbcGitHubLinkInfoService(
            gitHubClient,
            linksParsingService,
            jdbcGitHubLinkInfoRepository,
            jdbcLinkRepository
        );
    }

    @Bean
    public StackOverFlowLinkInfoService stackOverFlowLinkInfoService(
        StackOverflowClient stackOverflowClient,
        LinksParsingService linksParsingService,
        JdbcStackOverFlowLinkInfoRepository jdbcStackOverFlowLinkInfoRepository
    ) {
        return new JdbcStackOverFlowLinkInfoService(
            stackOverflowClient,
            linksParsingService,
            jdbcStackOverFlowLinkInfoRepository
        );
    }

    @Bean
    public LinkInfoService linkInfoService(
        GitHubLinkInfoService jdbcGitHubLinkInfoService,
        StackOverFlowLinkInfoService jdbcStackOverFlowLinkInfoService
    ) {
        return new LinkInfoService(jdbcGitHubLinkInfoService, jdbcStackOverFlowLinkInfoService);
    }

    @Bean
    public LinkService linkService(
        JdbcLinkRepository linkRepository,
        JdbcTgChatRepository tgChatRepository,
        LinkInfoService linkInfoService
    ) {
        return new JdbcLinkService(linkRepository, tgChatRepository, linkInfoService);
    }

    @Bean
    public TgChatService tgChatService(
        JdbcTgChatRepository tgChatRepository,
        JdbcLinkRepository jdbcLinkRepository
    ) {
        return new JdbcTgChatService(tgChatRepository, jdbcLinkRepository);
    }

}
