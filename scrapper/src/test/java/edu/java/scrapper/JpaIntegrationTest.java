package edu.java.scrapper;

import edu.java.domain.jpaRepository.JpaGitHubLinkInfoRepository;
import edu.java.domain.jpaRepository.JpaLinkRepository;
import edu.java.domain.jpaRepository.JpaStackOverFlowLinkInfoRepository;
import edu.java.domain.jpaRepository.JpaTgChatRepository;
import edu.java.service.jpa.JpaGitHubLinkInfoService;
import edu.java.service.jpa.JpaLinkService;
import edu.java.service.jpa.JpaStackOverFlowLinkInfoService;
import edu.java.service.jpa.JpaTgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"app.database-access-type=jpa", "app.useQueue=false"})
public abstract class JpaIntegrationTest extends IntegrationTest {

    @Autowired protected JpaTgChatRepository jpaTgChatRepository;
    @Autowired protected JpaLinkRepository jpaLinkRepository;
    @Autowired protected JpaGitHubLinkInfoRepository jpaGitHubLinkInfoRepository;
    @Autowired protected JpaStackOverFlowLinkInfoRepository jpaStackOverFlowLinkInfoRepository;
    @Autowired protected JpaTgChatService jpaTgChatService;
    @Autowired protected JpaLinkService jpaLinkService;
    @Autowired protected JpaGitHubLinkInfoService jpaGitHubLinkInfoService;
    @Autowired protected JpaStackOverFlowLinkInfoService jpaStackOverFlowLinkInfoService;

}
