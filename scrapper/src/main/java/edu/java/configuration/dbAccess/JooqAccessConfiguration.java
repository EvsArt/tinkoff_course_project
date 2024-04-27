package edu.java.configuration.dbAccess;

import edu.java.domain.jooqRepository.JooqGitHubLinkInfoRepository;
import edu.java.domain.jooqRepository.JooqLinkRepository;
import edu.java.domain.jooqRepository.JooqStackOverFlowLinkInfoRepository;
import edu.java.domain.jooqRepository.JooqTgChatRepository;
import edu.java.service.GitHubLinkInfoService;
import edu.java.service.LinkInfoService;
import edu.java.service.LinkService;
import edu.java.service.LinksParsingService;
import edu.java.service.StackOverFlowLinkInfoService;
import edu.java.service.TgChatService;
import edu.java.service.jooq.JooqGitHubLinkInfoService;
import edu.java.service.jooq.JooqLinkService;
import edu.java.service.jooq.JooqStackOverFlowLinkInfoService;
import edu.java.service.jooq.JooqTgChatService;
import edu.java.servicesClient.GitHubClient;
import edu.java.servicesClient.StackOverflowClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {

    @Bean
    public GitHubLinkInfoService gitHubLinkInfoService(
        GitHubClient gitHubClient,
        LinksParsingService linksParsingService,
        JooqGitHubLinkInfoRepository jooqGitHubLinkInfoRepository,
        JooqLinkRepository jooqLinkRepository
    ) {
        return new JooqGitHubLinkInfoService(
            gitHubClient,
            linksParsingService,
            jooqGitHubLinkInfoRepository,
            jooqLinkRepository
        );
    }

    @Bean
    public StackOverFlowLinkInfoService stackOverFlowLinkInfoService(
        StackOverflowClient stackOverflowClient,
        LinksParsingService linksParsingService,
        JooqStackOverFlowLinkInfoRepository jooqStackOverFlowLinkInfoRepository
    ) {
        return new JooqStackOverFlowLinkInfoService(
            stackOverflowClient,
            linksParsingService,
            jooqStackOverFlowLinkInfoRepository
        );
    }

    @Bean
    public LinkInfoService linkInfoService(
        GitHubLinkInfoService jooqGitHubLinkInfoService,
        StackOverFlowLinkInfoService jooqStackOverFlowLinkInfoService
    ) {
        return new LinkInfoService(jooqGitHubLinkInfoService, jooqStackOverFlowLinkInfoService);
    }

    @Bean
    public LinkService linkService(
        JooqLinkRepository linkRepository,
        JooqTgChatRepository tgChatRepository,
        LinkInfoService linkInfoService
    ) {
        return new JooqLinkService(linkRepository, tgChatRepository, linkInfoService);
    }

    @Bean
    public TgChatService tgChatService(
        JooqTgChatRepository tgChatRepository,
        JooqLinkRepository jooqLinkRepository
    ) {
        return new JooqTgChatService(tgChatRepository, jooqLinkRepository);
    }

}
