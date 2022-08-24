package org.danylo.repository;

import org.danylo.mapper.CountryRowMapper;
import org.danylo.model.Country;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class CountryRepository extends BaseRepository {

    public List<Country> getAll() {
        String sql = "SELECT * FROM country";
        return namedParameterJdbcTemplate.query(sql, new CountryRowMapper());
    }

    public Country getById(int id) {
        String sql = "SELECT * FROM country WHERE id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new CountryRowMapper());
    }
}
