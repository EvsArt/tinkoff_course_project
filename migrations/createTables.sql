--liquibase formatted sql
--changeset evsart:tg_chat
create table tg_chat (
    id              bigint generated always as identity,
    chat_id         bigint                   not null,
    name            text                     not null,

    primary key (id),
    unique (chat_id)
);

--changeset evsart:link
create table link (
    id                  bigint generated always as identity,
    url                 text                     not null,
    name                text,

    created_at          timestamp with time zone not null,
    last_update_time    timestamp with time zone not null,
    last_check_time     timestamp with time zone not null,

    primary key (id),
    unique (url)
);

--changeset evsart:link_tg_chat
create table link_tg_chat (
    tg_chat_id bigint references tg_chat(id),
    link_id bigint references link(id)
)
