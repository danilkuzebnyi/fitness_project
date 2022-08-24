package org.danylo.mapper;

import org.danylo.model.Trainer;
import org.danylo.model.WorkingTime;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;

public class WorkingTimeRowMapper implements RowMapper<WorkingTime> {
    @Override
    public WorkingTime mapRow(ResultSet rs, int rowNum) throws SQLException {
        WorkingTime workingTime = new WorkingTime();
        workingTime.setId(rs.getInt("id"));
        Trainer trainer = new Trainer();
        trainer.setId(rs.getInt("trainer_id"));
        workingTime.setTrainer(trainer);
        workingTime.setDayOfWeek(DayOfWeek.of(rs.getInt("day_id")));
        workingTime.setHoursFrom(rs.getTime("hours_from"));
        workingTime.setHoursTo(rs.getTime("hours_to"));

        return workingTime;
    }
}
