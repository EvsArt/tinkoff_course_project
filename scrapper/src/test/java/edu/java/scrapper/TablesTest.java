package edu.java.scrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@TestPropertySource(properties = "app.useQueue=false")
public class TablesTest extends IntegrationTest {

    @Test
    void tableLinkExists() throws SQLException {
        Connection connection = POSTGRES.createConnection("");

        PreparedStatement linkStatement = connection.prepareStatement("select * from link");
        Throwable linkThrown = catchThrowable(linkStatement::executeQuery);

        assertThat(linkThrown).doesNotThrowAnyException();
    }

    @Test
    void tableTgChatExists() throws SQLException, InterruptedException {
        Connection connection = POSTGRES.createConnection("");

        PreparedStatement tgChatStatement = connection.prepareStatement("select * from tg_chat");
        Throwable tgChatThrown = catchThrowable(tgChatStatement::executeQuery);

        assertThat(tgChatThrown).doesNotThrowAnyException();
    }

    @Test
    void associationTableExists() throws SQLException, InterruptedException {
        Connection connection = POSTGRES.createConnection("");

        PreparedStatement associationTableStatement = connection.prepareStatement("select * from link_tg_chat");
        Throwable associationsThrown = catchThrowable(associationTableStatement::executeQuery);

        assertThat(associationsThrown).doesNotThrowAnyException();
    }

}
