package org.danylo.repository;

import org.danylo.mapper.FitnessClubRowMapper;
import org.danylo.mapper.TrainerRowMapper;
import org.danylo.model.FitnessClub;
import org.danylo.model.Trainer;
import org.danylo.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class FitnessClubRepository extends TrainerRepository {

    public FitnessClub getClubByUser(User user) {
        try {
            String sql = "SELECT * FROM fitness_club WHERE user_id=:id";
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", user.getId());
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new FitnessClubRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("No such trainer");
        }
    }

    public List<Trainer> getTrainersByClub(FitnessClub fitnessClub) {
        try {
            String sql = "SELECT * FROM trainer WHERE fitness_club_id=:id";
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", fitnessClub.getId());
            return namedParameterJdbcTemplate.query(sql, namedParameters, new TrainerRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("No such trainers");
        }
    }
}
