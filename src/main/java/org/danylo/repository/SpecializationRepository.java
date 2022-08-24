package org.danylo.repository;

import org.danylo.mapper.SpecializationRowMapper;
import org.danylo.model.Specialization;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class SpecializationRepository extends BaseRepository {

    public List<Specialization> showAllSpecializations() {
        String sql = "SELECT * FROM specialization";
        return namedParameterJdbcTemplate.query(sql, new SpecializationRowMapper());
    }

    public List<Specialization> getTrainerSpecializations(int id) {
        String sql = "SELECT name FROM specialization s" +
                " JOIN trainer_specialization ts on s.id = ts.specialization_id" +
                " JOIN trainer t on t.id = ts.trainer_id" +
                " WHERE t.id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.query(sql, namedParameters, new SpecializationRowMapper());
    }
}
