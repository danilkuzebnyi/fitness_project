package org.danylo.mapper;

import org.danylo.model.Country;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountryRowMapper implements RowMapper<Country> {
    @Override
    public Country mapRow(ResultSet rs, int rowNum) throws SQLException {
        Country country = new Country();
        country.setId(rs.getInt("id"));
        country.setName(rs.getString("name"));
        country.setCode(rs.getString("code"));

        return country;
    }
}
