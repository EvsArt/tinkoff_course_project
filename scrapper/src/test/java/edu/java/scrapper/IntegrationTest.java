package edu.java.scrapper;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import edu.java.domain.jdbcRepository.JdbcAssociativeTableRepository;
import edu.java.domain.jdbcRepository.JdbcGitHubLinkInfoRepository;
import edu.java.domain.jdbcRepository.JdbcLinkRepository;
import edu.java.domain.jdbcRepository.JdbcStackOverFlowLinkInfoRepository;
import edu.java.domain.jdbcRepository.JdbcTgChatRepository;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
public abstract class IntegrationTest {

    private static final Path migrationsPath =
        Path.of(".").toAbsolutePath()
            .getParent()
            .getParent()
            .resolve("migrations");
    private static final String changeLogFileName = "master.yaml";

    public static PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");
        POSTGRES.start();

        try {
            runMigrations(POSTGRES);
        } catch (FileNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runMigrations(JdbcDatabaseContainer<?> c) throws FileNotFoundException, SQLException {
        ResourceAccessor accessor = new DirectoryResourceAccessor(migrationsPath);
        System.out.println(c.getJdbcUrl());
        DatabaseConnection connection = new JdbcConnection(
            DriverManager.getConnection(
                c.getJdbcUrl(),
                c.getUsername(),
                c.getPassword()
            )
        );

        try (
            Liquibase liquibase = new Liquibase(
                changeLogFileName,
                accessor,
                connection
            )
        ) {
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
