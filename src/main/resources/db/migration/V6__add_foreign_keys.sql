alter table booking
    add constraint booking_trainer_id_fk
        foreign key (trainer_id) references trainer (id)
            on update set null on delete set null;
alter table booking
    add constraint booking_users_id_fk
        foreign key (user_id) references users (id)
            on update set null on delete set null;

alter table fitness_club
    add constraint fitness_club_users_id_fk
        foreign key (user_id) references users (id)
            on update set null on delete set null;

alter table trainer
    add constraint trainer_users_id_fk
        foreign key (user_id) references users (id)
            on update set null on delete set null;
alter table trainer
    add constraint trainer_fitness_club_id_fk
        foreign key (fitness_club_id) references fitness_club (id)
            on update set null on delete set null;

alter table trainer_rating
    add constraint trainer_rating_trainer_id_fk
        foreign key (trainer_id) references trainer (id)
            on update set null on delete set null;

alter table trainer_specialization
    add constraint trainer_specialization_trainer_id_fk
        foreign key (trainer_id) references trainer (id)
            on update set null on delete set null;
alter table trainer_specialization
    add constraint trainer_specialization_specialization_id_fk
        foreign key (specialization_id) references specialization (id)
            on update set null on delete set null;

alter table trainer_working_hours
    add constraint trainer_working_hours_trainer_id_fk
        foreign key (trainer_id) references trainer (id)
            on update set null on delete set null;

alter table users
    add constraint users_country_id_fk
        foreign key (country_id) references country (id)
            on update set null on delete set null;