package org.danylo.repository;

import org.danylo.model.Trainer;
import org.danylo.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class TrainerRepository extends UserRepository {
    public List<Trainer> showAll() {
        String sql = "SELECT t.id, experience, description, image, price, AVG(rating) AS rating, COUNT(rating) AS count " +
                     "FROM trainer t " +
                         "LEFT JOIN trainer_rating tr ON t.id = tr.trainer_id " +
                     "GROUP BY t.id, price, experience, image, description";

        return namedParameterJdbcTemplate.query(sql, new TrainerRatingRowMapper());
    }

    public List<Trainer> showTrainersBySpecialization(String specialization) {
        String sql = "SELECT t.id, experience, description, image, price, AVG(rating) AS rating, COUNT(rating) AS count " +
                     "FROM trainer t " +
                        "LEFT JOIN trainer_rating tr on t.id = tr.trainer_id " +
                        "JOIN trainer_specialization ts ON t.id = ts.trainer_id " +
                        "JOIN specialization s ON s.id = ts.specialization_id " +
                     "WHERE s.name = :specialization " +
                     "GROUP BY t.id, price, experience, image, description";

        SqlParameterSource namedParameters = new MapSqlParameterSource("specialization", specialization);

        return namedParameterJdbcTemplate.query(sql, namedParameters, new TrainerRatingRowMapper());
    }

    public Trainer getTrainerByUser(User user) {
        try {
            String sql = "SELECT * FROM trainer t WHERE user_id=:id";
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", user.getId());
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new TrainerRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("No such trainer");
        }
    }

    public Trainer getById(int id) {
        String sql = "SELECT t.id, experience, description, image, price, AVG(rating) AS rating, COUNT(rating) AS count " +
                     "FROM trainer t " +
                         "LEFT JOIN trainer_rating tr ON t.id = tr.trainer_id " +
                     "WHERE t.id=:id " +
                     "GROUP BY t.id, price, experience, image, description";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new TrainerRatingRowMapper());
    }

    public void updateTrainer(Trainer trainer) {
        String sql = "UPDATE trainer SET price=:price WHERE id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource().
                addValue("price", trainer.getPrice()).
                addValue("id", trainer.getId());

        namedParameterJdbcTemplate.execute(sql, namedParameters, PreparedStatement::execute);
    }

    @PreAuthorize("hasAuthority('rate')")
    public void saveRating(int trainerId, int rating) {
        String sql = "INSERT INTO trainer_rating(trainer_id, rating) VALUES (:trainer_id, :rating)";
        SqlParameterSource namedParameters = new MapSqlParameterSource().
                addValue("trainer_id", trainerId).
                addValue("rating", rating);

        namedParameterJdbcTemplate.execute(sql, namedParameters, PreparedStatement::execute);
    }

    @PreAuthorize("hasAuthority('book')")
    public void bookClientWithTrainer(int userId, int trainerId) {
        String sql = "INSERT INTO booking(user_id, trainer_id) VALUES (:user_id, :trainer_id)";
        SqlParameterSource namedParameters = new MapSqlParameterSource().
                addValue("user_id", userId).
                addValue("trainer_id", trainerId);

        namedParameterJdbcTemplate.execute(sql, namedParameters, PreparedStatement::execute);
    }

    public Integer getNumberOfClients(Trainer trainer) {
        String sql = "SELECT COUNT(user_id) AS count " +
                         "FROM booking " +
                     "WHERE trainer_id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", trainer.getId());

        return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
    }

    public List<Integer> getUsersWhoTrainedWithTrainer (int trainerId) {
        String sql = "SELECT user_id " +
                "FROM booking " +
                "WHERE trainer_id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", trainerId);

        return namedParameterJdbcTemplate.queryForList(sql, namedParameters, Integer.class);
    }
}