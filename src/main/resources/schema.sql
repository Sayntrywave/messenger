create table users
(
    id              integer generated by default as identity
        primary key,
    login           varchar(30)                                     not null
        unique,
    password        varchar(80)                                     not null,
    name            varchar(30),
    email           varchar(50)                                     not null
        unique,
    is_in_ban       boolean                                         not null,
    is_only_friends boolean                                         not null,
    hide_friends    boolean                                         not null,
    surname         varchar(40) default 'иванов'::character varying not null
);


create table messages
(
    id           integer generated by default as identity
        primary key,
    user_from_id integer
        references public.users
            on delete cascade,
    user_to_id   integer
        references public.users
            on delete cascade,
    message      varchar(350)
);


create table tokens
(
    id         integer generated by default as identity
        primary key,
    token      varchar(400) not null,
    is_expired boolean
);

create table users_to_be_confirmed
(
    id              integer generated by default as identity
        primary key,
    login           varchar(30) not null
        unique,
    password        varchar(80) not null,
    name            varchar(30),
    email           varchar(50) not null
        unique,
    is_in_ban       boolean,
    surname         varchar(80) not null,
    is_only_friends boolean     not null,
    hide_friends    boolean     not null
);



create table friends_request
(
    id           integer generated by default as identity
        primary key,
    user_from_id integer
        references public.users
            on delete cascade,
    user_to_id   integer
        references public.users
            on delete cascade
);


create table friends
(
    id        integer generated by default as identity
        constraint friend_pkey
            primary key,
    first_id  integer
        constraint friend_first_id_fkey
            references public.users
            on delete cascade,
    second_id integer
        constraint friend_second_id_fkey
            references public.users
            on delete cascade
);



