create table t_pass
(
    count         int                         not null,
    pass_id       int auto_increment
        primary key,
    user_group_id int                         not null,
    expired_at    datetime(6)                 not null,
    started_at    datetime(6)                 not null,
    pass_status   enum ('COMPLETED', 'READY') null
);


create table t_user_group
(
    user_group_id   int          not null,
    created_at      datetime(6)  not null,
    modified_at     datetime(6)  not null,
    user_id         bigint       not null,
    user_group_name varchar(255) not null,
    primary key (user_group_id, user_id)
);

create table t_user_pass
(
    pass_id         int                         not null,
    remaining_count int                         not null,
    user_pass_id    int auto_increment
        primary key,
    created_at      datetime(6)                 not null,
    expired_at      datetime(6)                 not null,
    modified_at     datetime(6)                 not null,
    started_at      datetime(6)                 not null,
    user_id         bigint                      not null,
    pass_status     enum ('COMPLETED', 'READY') null
);



