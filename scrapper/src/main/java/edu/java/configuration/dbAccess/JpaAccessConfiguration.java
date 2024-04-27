package edu.java.configuration.dbAccess;

import edu.java.servicesClient.GitHubClient;
import edu.java.servicesClient.StackOverflowClient;
import edu.java.domain.jpaRepository.JpaGitHubLinkInfoRepository;
import edu.java.domain.jpaRepository.JpaLinkRepository;
import edu.java.domain.jpaRepository.JpaStackOverFlowLinkInfoRepository;
import edu.java.domain.jpaRepository.JpaTgChatRepository;
import edu.java.service.GitHubLinkInfoService;
import edu.java.service.LinkInfoService;
import edu.java.service.LinkService;
import edu.java.service.LinksParsingService;
import edu.java.service.StackOverFlowLinkInfoService;
import edu.java.service.TgChatService;
import edu.java.service.jpa.JpaGitHubLinkInfoService;
import edu.java.service.jpa.JpaLinkService;
import edu.java.service.jpa.JpaStackOverFlowLinkInfoService;
import edu.java.service.jpa.JpaTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {

    @Bean
    public TgChatService tgChatService(
        JpaTgChatRepository tgChatRepository
    ) {
        return new JpaTgChatService(tgChatRepository);
    }

    @Bean
    public GitHubLinkInfoService gitHubLinkInfoService(
        JpaGitHubLinkInfoRepository jpaGitHubLinkInfoRepository,
        GitHubClient client,
        JpaLinkRepository linkRepository,
        LinksParsingService linksParsingService
    ) {
        return new JpaGitHubLinkInfoService(jpaGitHubLinkInfoRepository, linkRepository, client, linksParsingService);
    }

    @Bean
    public StackOverFlowLinkInfoService stackOverFlowLinkInfoService(
        JpaStackOverFlowLinkInfoRepository jpaStackOverFlowLinkInfoRepository,
        JpaLinkRepository jpaLinkRepository,
        StackOverflowClient client,
        LinksParsingService linksParsingService
    ) {
        return new JpaStackOverFlowLinkInfoService(
            jpaStackOverFlowLinkInfoRepository,
            jpaLinkRepository,
            client,
            linksParsingService
        );
    }

    @Bean
    public LinkInfoService linkInfoService(
        GitHubLinkInfoService gitHubLinkInfoService,
        StackOverFlowLinkInfoService stackOverFlowLinkInfoService
    ) {
        return new LinkInfoService(gitHubLinkInfoService, stackOverFlowLinkInfoService);
    }

    @Bean
    public LinkService linkService(
        JpaLinkRepository linkRepository,
        JpaTgChatRepository tgChatRepository
    ) {
        return new JpaLinkService(linkRepository, tgChatRepository);
    }

}
