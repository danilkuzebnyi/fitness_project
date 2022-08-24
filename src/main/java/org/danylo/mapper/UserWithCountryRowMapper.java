package org.danylo.mapper;

import org.danylo.model.Country;
import org.danylo.model.User;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserWithCountryRowMapper extends UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = super.mapRow(rs, rowNum);
        Country country = new Country();
        country.setId(rs.getInt("country_id"));
        country.setName(rs.getString("name"));
        country.setCode(rs.getString("code"));
        if (user != null) {
            user.setCountry(country);
        }

        return user;
    }
}
