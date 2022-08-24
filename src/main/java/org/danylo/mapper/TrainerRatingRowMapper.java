package org.danylo.mapper;

import org.danylo.model.Rating;
import org.danylo.model.Trainer;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrainerRatingRowMapper extends TrainerRowMapper implements RowMapper<Trainer> {
    @Override
    public Trainer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Trainer trainer = super.mapRow(rs, rowNum);
        Rating rating = new Rating();
        rating.setTrainer(trainer);
        rating.setValue(rs.getFloat("rating"));
        rating.setCount(rs.getInt("count"));
        if (trainer != null) {
            trainer.setRating(rating);
        }

        return trainer;
    }
}
