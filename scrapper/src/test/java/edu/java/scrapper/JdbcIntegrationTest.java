package edu.java.scrapper;

import edu.java.domain.jdbcRepository.JdbcAssociativeTableRepository;
import edu.java.domain.jdbcRepository.JdbcGitHubLinkInfoRepository;
import edu.java.domain.jdbcRepository.JdbcLinkRepository;
import edu.java.domain.jdbcRepository.JdbcStackOverFlowLinkInfoRepository;
import edu.java.domain.jdbcRepository.JdbcTgChatRepository;
import edu.java.service.jdbc.JdbcGitHubLinkInfoService;
import edu.java.service.jdbc.JdbcLinkService;
import edu.java.service.jdbc.JdbcStackOverFlowLinkInfoService;
import edu.java.service.jdbc.JdbcTgChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"app.database-access-type=jdbc", "app.useQueue=false"})
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
