package org.danylo.mapper;

import org.danylo.model.Specialization;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpecializationRowMapper implements RowMapper<Specialization> {
    @Override
    public Specialization mapRow(ResultSet rs, int rowNum) throws SQLException {
        Specialization specialization = new Specialization();
        specialization.setName(rs.getString("name"));

        return specialization;
    }
}
