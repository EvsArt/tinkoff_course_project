package edu.java.scrapper;

import edu.java.domain.jdbcRepository.*;
import edu.java.service.jdbc.JdbcGitHubLinkInfoService;
import edu.java.service.jdbc.JdbcLinkService;
import edu.java.service.jdbc.JdbcStackOverFlowLinkInfoService;
import edu.java.service.jdbc.JdbcTgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "app.database-access-type=jdbc")
public abstract class JdbcIntegrationTest extends IntegrationTest {

    @Autowired protected JdbcTgChatRepository jdbcTgChatRepository;
    @Autowired protected JdbcLinkRepository jdbcLinkRepository;
    @Autowired protected JdbcAssociativeTableRepository jdbcAssociativeTableRepository;
    @Autowired protected JdbcGitHubLinkInfoRepository jdbcGitHubLinkInfoRepository;
    @Autowired protected JdbcStackOverFlowLinkInfoRepository jdbcStackOverFlowLinkInfoRepository;
    @Autowired protected JdbcTgChatService jdbcTgChatService;
    @Autowired protected JdbcLinkService jdbcLinkService;
    @Autowired protected JdbcGitHubLinkInfoService jdbcGitHubLinkInfoService;
    @Autowired protected JdbcStackOverFlowLinkInfoService jdbcStackOverFlowLinkInfoService;

}
