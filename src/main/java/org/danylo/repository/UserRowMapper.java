package org.danylo.repository;

import org.danylo.model.Country;
import org.danylo.model.Role;
import org.danylo.model.User;
import org.springframework.jdbc.core.RowMapper;
import java.sql.*;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setUsername(rs.getString("username"));
        Country country = new Country();
        country.setId(rs.getInt("country_id"));
        country.setName(rs.getString("name"));
        country.setCode(rs.getString("code"));
        user.setCountry(country);
        user.setTelephoneNumber(rs.getString("phone"));
        user.setPassword(rs.getString("password"));
        user.setRole(Role.valueOf(rs.getString("role")));

        return user;
    }
}
