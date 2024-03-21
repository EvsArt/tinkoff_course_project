/*
 * This file is generated by jOOQ.
 */

package edu.java.domain.jooq;

import edu.java.domain.jooq.tables.GithubLinkInfo;
import edu.java.domain.jooq.tables.Link;
import edu.java.domain.jooq.tables.LinkTgChat;
import edu.java.domain.jooq.tables.StackoverflowLinkInfo;
import edu.java.domain.jooq.tables.TgChat;
import java.util.Arrays;
import java.util.List;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.NotNull;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape"})
public class DefaultSchema extends SchemaImpl {

    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();
    private static final long serialVersionUID = 1L;
    public final GithubLinkInfo GITHUB_LINK_INFO = GithubLinkInfo.GITHUB_LINK_INFO;

    public final Link LINK = Link.LINK;

    public final LinkTgChat LINK_TG_CHAT = LinkTgChat.LINK_TG_CHAT;

    public final StackoverflowLinkInfo STACKOVERFLOW_LINK_INFO = StackoverflowLinkInfo.STACKOVERFLOW_LINK_INFO;

    public final TgChat TG_CHAT = TgChat.TG_CHAT;

    private DefaultSchema() {
        super("", null);
    }

    @Override
    @NotNull
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    @NotNull
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            GithubLinkInfo.GITHUB_LINK_INFO,
            Link.LINK,
            LinkTgChat.LINK_TG_CHAT,
            StackoverflowLinkInfo.STACKOVERFLOW_LINK_INFO,
            TgChat.TG_CHAT
        );
    }
}
