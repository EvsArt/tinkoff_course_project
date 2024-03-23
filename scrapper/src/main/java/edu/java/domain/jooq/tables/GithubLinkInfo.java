/*
 * This file is generated by jOOQ.
 */
package edu.java.domain.jooq.tables;


import edu.java.domain.jooq.DefaultSchema;
import edu.java.domain.jooq.tables.records.GithubLinkInfoRecord;

import javax.annotation.processing.Generated;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class GithubLinkInfo extends TableImpl<GithubLinkInfoRecord> {

    private static final long serialVersionUID = 1L;

    public static final GithubLinkInfo GITHUB_LINK_INFO = new GithubLinkInfo();

    @Override
    @NotNull
    public Class<GithubLinkInfoRecord> getRecordType() {
        return GithubLinkInfoRecord.class;
    }

    public final TableField<GithubLinkInfoRecord, Long> ID = createField(DSL.name("ID"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    public final TableField<GithubLinkInfoRecord, Long> LINK_ID = createField(DSL.name("LINK_ID"), SQLDataType.BIGINT.nullable(false), this, "");

    public final TableField<GithubLinkInfoRecord, Long> LAST_EVENT_ID = createField(DSL.name("LAST_EVENT_ID"), SQLDataType.BIGINT.nullable(false), this, "");

    private GithubLinkInfo(Name alias, Table<GithubLinkInfoRecord> aliased) {
        this(alias, aliased, null);
    }

    private GithubLinkInfo(Name alias, Table<GithubLinkInfoRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public GithubLinkInfo(String alias) {
        this(DSL.name(alias), GITHUB_LINK_INFO);
    }

    public GithubLinkInfo(Name alias) {
        this(alias, GITHUB_LINK_INFO);
    }

    public GithubLinkInfo() {
        this(DSL.name("GITHUB_LINK_INFO"), null);
    }

    @Override
    @Nullable
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    @NotNull
    public GithubLinkInfo as(String alias) {
        return new GithubLinkInfo(DSL.name(alias), this);
    }

    @Override
    @NotNull
    public GithubLinkInfo as(Name alias) {
        return new GithubLinkInfo(alias, this);
    }

    @Override
    @NotNull
    public GithubLinkInfo as(Table<?> alias) {
        return new GithubLinkInfo(alias.getQualifiedName(), this);
    }

    @Override
    @NotNull
    public GithubLinkInfo rename(String name) {
        return new GithubLinkInfo(DSL.name(name), null);
    }

    @Override
    @NotNull
    public GithubLinkInfo rename(Name name) {
        return new GithubLinkInfo(name, null);
    }

    @Override
    @NotNull
    public GithubLinkInfo rename(Table<?> name) {
        return new GithubLinkInfo(name.getQualifiedName(), null);
    }
}
