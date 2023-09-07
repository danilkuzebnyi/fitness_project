DROP TABLE trainer_rating;

CREATE TABLE trainer_rating
(
    trainer_id INT
        CONSTRAINT trainer_rating_trainer_id_fk
            REFERENCES trainer
            ON UPDATE SET NULL ON DELETE SET NULL,
    user_id    INT
        CONSTRAINT trainer_rating_user_id_fk
            references users
            ON UPDATE SET NULL ON DELETE SET NULL,
    rating     INT,
    CONSTRAINT trainer_rating_pk
        PRIMARY KEY (trainer_id, user_id)
);