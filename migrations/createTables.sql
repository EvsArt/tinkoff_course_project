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
    tg_chat_id bigint references tg_chat(id)  on delete cascade,
    link_id bigint references link(id)  on delete cascade
)

--changeset evsart:github_link_info
create table github_link_info (
    id bigint generated always as identity,
    link_id bigint not null references link(id) on delete cascade,

    last_event_id bigint not null,

    unique (link_id),
    primary key(id)
)

--changeset evsart:stackoverflow_link_info
create table stackoverflow_link_info (
    id bigint generated always as identity,
    link_id bigint not null references link(id)  on delete cascade,

    answers_count integer not null,

    unique (link_id),
    primary key(id)
)
