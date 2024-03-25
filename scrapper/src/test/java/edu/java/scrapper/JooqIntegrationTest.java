package edu.java.scrapper;

import edu.java.domain.jooqRepository.JooqAssociativeTableRepository;
import edu.java.domain.jooqRepository.JooqGitHubLinkInfoRepository;
import edu.java.domain.jooqRepository.JooqLinkRepository;
import edu.java.domain.jooqRepository.JooqStackOverFlowLinkInfoRepository;
import edu.java.domain.jooqRepository.JooqTgChatRepository;
import edu.java.service.jooq.JooqGitHubLinkInfoService;
import edu.java.service.jooq.JooqLinkService;
import edu.java.service.jooq.JooqStackOverFlowLinkInfoService;
import edu.java.service.jooq.JooqTgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "app.database-access-type=jooq")
public abstract class JooqIntegrationTest extends IntegrationTest {

    @Autowired protected JooqTgChatRepository jooqTgChatRepository;
    @Autowired protected JooqLinkRepository jooqLinkRepository;
    @Autowired protected JooqAssociativeTableRepository jooqAssociativeTableRepository;
    @Autowired protected JooqGitHubLinkInfoRepository jooqGitHubLinkInfoRepository;
    @Autowired protected JooqStackOverFlowLinkInfoRepository jooqStackOverFlowLinkInfoRepository;
    @Autowired protected JooqTgChatService jooqTgChatService;
    @Autowired protected JooqLinkService jooqLinkService;
    @Autowired protected JooqGitHubLinkInfoService jooqGitHubLinkInfoService;
    @Autowired protected JooqStackOverFlowLinkInfoService jooqStackOverFlowLinkInfoService;

}
