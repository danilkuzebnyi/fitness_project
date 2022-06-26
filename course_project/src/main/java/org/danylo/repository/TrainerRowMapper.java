package org.danylo.repository;

import org.danylo.model.Trainer;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrainerRowMapper implements RowMapper<Trainer> {
    @Override
    public Trainer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Trainer trainer = new Trainer();
        trainer.setId(rs.getInt("id"));
        trainer.setDescription(rs.getString("description"));
        trainer.setExperience(rs.getInt("experience"));
        trainer.setPrice(rs.getInt("price"));
        trainer.setImage(rs.getString("image"));

        return trainer;
    }
}
