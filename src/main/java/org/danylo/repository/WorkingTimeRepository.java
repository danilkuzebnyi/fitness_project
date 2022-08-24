package org.danylo.repository;

import org.danylo.mapper.WorkingTimeRowMapper;
import org.danylo.model.Trainer;
import org.danylo.model.WorkingTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.time.DayOfWeek;
import java.util.List;

@Repository
public class WorkingTimeRepository extends TrainerRepository {
    private static final Logger logger = LoggerFactory.getLogger(WorkingTimeRepository.class);

    public void save(WorkingTime workingTime) {
        if (getWorkingTimeOfTrainerByDayOfWeek(workingTime).getTrainer() == null) {
            String sql = "INSERT INTO trainer_working_hours(trainer_id, day_id, hours_from, hours_to) " +
                    "VALUES(:trainer_id, :day_id, :hours_from, :hours_to)";

            SqlParameterSource namedParameters = new MapSqlParameterSource().
                    addValue("trainer_id", workingTime.getTrainer().getId()).
                    addValue("day_id", workingTime.getDayOfWeek().getValue()).
                    addValue("hours_from", workingTime.getHoursFrom()).
                    addValue("hours_to", workingTime.getHoursTo());

            namedParameterJdbcTemplate.execute(sql, namedParameters, PreparedStatement::execute);
        } else {
            update(workingTime);
        }
    }

    private void update(WorkingTime workingTime) {
        String sql = "UPDATE trainer_working_hours SET day_id=:day_id, hours_from=:hours_from, " +
                "hours_to=:hours_to WHERE trainer_id=:trainer_id AND day_id=:day_id";

        SqlParameterSource namedParameters = new MapSqlParameterSource().
                addValue("trainer_id", workingTime.getTrainer().getId()).
                addValue("day_id", workingTime.getDayOfWeek().getValue()).
                addValue("hours_from", workingTime.getHoursFrom()).
                addValue("hours_to", workingTime.getHoursTo());

        namedParameterJdbcTemplate.execute(sql, namedParameters, PreparedStatement::execute);
    }

    public WorkingTime getWorkingTimeOfTrainerByDayOfWeek(Trainer trainer, DayOfWeek dayOfWeek) {
        try {
            String sql = "SELECT * FROM trainer_working_hours WHERE trainer_id=:trainer_id AND day_id=:day_id";

            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("trainer_id", trainer.getId())
                    .addValue("day_id", dayOfWeek.getValue());
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new WorkingTimeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.info("get empty result from method getWorkingTimeOfTrainerByDayOfWeek");
            return new WorkingTime();
        }
    }

    public WorkingTime getWorkingTimeOfTrainerByDayOfWeek(WorkingTime workingTime) {
        try {
            String sql = "SELECT * FROM trainer_working_hours WHERE trainer_id=:trainer_id AND day_id=:day_id";

            SqlParameterSource namedParameters = new MapSqlParameterSource()
                    .addValue("trainer_id", workingTime.getTrainer().getId())
                    .addValue("day_id", workingTime.getDayOfWeek().getValue());

            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new WorkingTimeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logger.info("get empty result from method getWorkingTimeOfTrainerByDayOfWeek");
            return new WorkingTime();
        }
    }

    public List<WorkingTime> getWorkingDaysOfTrainer(Trainer trainer) {
        String sql = "SELECT * FROM trainer_working_hours WHERE trainer_id=:trainer_id ORDER BY day_id";

        SqlParameterSource namedParameters = new MapSqlParameterSource
                ("trainer_id", trainer.getId());

        return namedParameterJdbcTemplate.query(sql, namedParameters, new WorkingTimeRowMapper());
    }

    public void deleteWorkingHoursOfTrainerByDayOfWeek(int trainerId, DayOfWeek dayOfWeek) {
        String sql = "DELETE FROM trainer_working_hours WHERE trainer_id=:trainerId AND day_id=:dayId";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("trainerId", trainerId)
                .addValue("dayId", dayOfWeek.getValue());

        namedParameterJdbcTemplate.execute(sql, namedParameters, PreparedStatement::execute);
    }
}
