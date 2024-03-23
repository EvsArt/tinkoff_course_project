/*
 * This file is generated by jOOQ.
 */
package edu.java.domain.jooq.tables;


import edu.java.domain.jooq.DefaultSchema;
import edu.java.domain.jooq.tables.records.TgChatRecord;

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
public class TgChat extends TableImpl<TgChatRecord> {

    private static final long serialVersionUID = 1L;

    public static final TgChat TG_CHAT = new TgChat();

    @Override
    @NotNull
    public Class<TgChatRecord> getRecordType() {
        return TgChatRecord.class;
    }

    public final TableField<TgChatRecord, Long> ID = createField(DSL.name("ID"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    public final TableField<TgChatRecord, Long> CHAT_ID = createField(DSL.name("CHAT_ID"), SQLDataType.BIGINT.nullable(false), this, "");

    public final TableField<TgChatRecord, String> NAME = createField(DSL.name("NAME"), SQLDataType.VARCHAR(1000000000).nullable(false), this, "");

    private TgChat(Name alias, Table<TgChatRecord> aliased) {
        this(alias, aliased, null);
    }

    private TgChat(Name alias, Table<TgChatRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public TgChat(String alias) {
        this(DSL.name(alias), TG_CHAT);
    }

    public TgChat(Name alias) {
        this(alias, TG_CHAT);
    }

    public TgChat() {
        this(DSL.name("TG_CHAT"), null);
    }

    @Override
    @Nullable
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    @NotNull
    public TgChat as(String alias) {
        return new TgChat(DSL.name(alias), this);
    }

    @Override
    @NotNull
    public TgChat as(Name alias) {
        return new TgChat(alias, this);
    }

    @Override
    @NotNull
    public TgChat as(Table<?> alias) {
        return new TgChat(alias.getQualifiedName(), this);
    }

    @Override
    @NotNull
    public TgChat rename(String name) {
        return new TgChat(DSL.name(name), null);
    }

    @Override
    @NotNull
    public TgChat rename(Name name) {
        return new TgChat(name, null);
    }

    @Override
    @NotNull
    public TgChat rename(Table<?> name) {
        return new TgChat(name.getQualifiedName(), null);
    }
}
