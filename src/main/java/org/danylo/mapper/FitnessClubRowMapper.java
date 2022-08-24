package org.danylo.mapper;

import org.danylo.model.FitnessClub;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FitnessClubRowMapper implements RowMapper<FitnessClub> {
    @Override
    public FitnessClub mapRow(ResultSet rs, int rowNum) throws SQLException {
        FitnessClub fitnessClub = new FitnessClub();
        fitnessClub.setId(rs.getInt("id"));
        fitnessClub.setName(rs.getString("name"));

        return fitnessClub;
    }
}
