create table users
(
    id         serial
        constraint user_pk
            primary key,
    username   varchar                                          not null,
    password   varchar                                          not null,
    phone      varchar(13)                                      not null,
    first_name varchar,
    last_name  varchar,
    role       varchar(30)
);
create unique index user_id_uindex
    on users (id);
create unique index user_password_uindex
    on users (password);
create unique index user_username_uindex
    on users (username);


create table trainer
(
    id              serial
        constraint trainer_pk
            primary key,
    experience      integer,
    fitness_club_id integer,
    description     varchar,
    user_id         integer,
    image           varchar,
    price           integer
);
create unique index trainer_id_uindex
    on trainer (id);


create table booking
(
    id         serial
        constraint booking_pk
            primary key,
    user_id    int not null,
    trainer_id int
);
create unique index booking_id_uindex
    on booking (id);


create table fitness_club
(
    id      serial
        constraint fitness_club_pk
            primary key,
    name    varchar not null,
    user_id int
);
create unique index fitness_club_id_uindex
    on fitness_club (id);


create table specialization
(
    id   serial
        constraint specialization_pk
            primary key,
    name varchar not null
);
create unique index specialization_id_uindex
    on specialization (id);


create table trainer_specialization
(
    trainer_id        integer not null,
    specialization_id integer not null,
    id                serial
        constraint trainer_specialization_pk
            primary key
);
create unique index trainer_specialization_id_uindex
    on trainer_specialization (id);


create table trainer_working_hours
(
    id         serial
        constraint trainer_working_hours_pk
            primary key,
    trainer_id integer,
    day_id     integer,
    hours_from time,
    hours_to   time
);
create unique index trainer_working_hours_id_uindex
    on trainer_working_hours (id);


create table trainer_rating
(
    id         serial
        constraint trainer_rating_pk
            primary key,
    trainer_id int,
    rating     int
);
create unique index trainer_rating_id_uindex
    on trainer_rating (id);