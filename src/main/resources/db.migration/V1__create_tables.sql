create extension if not exists "pgcrypto";


create table if not exists users
(
    id         uuid primary key default gen_random_uuid(),
    name       varchar(50) not null,
    surname    varchar(50) not null,
    patronymic varchar(50),
    phone      varchar(50) not null unique,
    email      varchar(50) not null unique,
    birth_date date        not null,
    password   varchar(50) not null
);

create table if not exists wallets
(
    number  bigserial primary key,
    balance integer not null,
    user_id uuid    not null,
    foreign key (user_id) references users (id)
);

create table if not exists tickets
(
    id               uuid primary key default gen_random_uuid(),
    creation_date    timestamp not null,
    amount           integer   not null,
    paid             integer   not null,
    sender_user_id   uuid      not null,
    receiver_user_id uuid      not null,
    foreign key (sender_user_id) references users (id),
    foreign key (receiver_user_id) references users (id)
)