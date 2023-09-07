package org.danylo.repository;

import org.danylo.mapper.UserRowMapper;
import org.danylo.mapper.UserWithCountryRowMapper;
import org.danylo.model.Country;
import org.danylo.model.Trainer;
import org.danylo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class UserRepository extends BaseRepository {
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void saveUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, username, password, phone, role, country_id, " +
                "activation_code, status) " +
                "VALUES(:firstName, :lastName, :username, :password, :phone, :role, :countryId, :activationCode, :status)";
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Country userCountry = user.getCountry();
        SqlParameterSource namedParameters = new MapSqlParameterSource().
                addValue("firstName", user.getFirstName()).
                addValue("lastName", user.getLastName()).
                addValue("username", user.getUsername()).
                addValue("password", user.getPassword()).
                addValue("phone", userCountry.getCode() + user.getTelephoneNumber()).
                addValue("role", user.getRole().toString()).
                addValue("countryId", userCountry.getId()).
                addValue("activationCode", user.getActivationCode()).
                addValue("status", user.getStatus().toString());

        namedParameterJdbcTemplate.execute(sql, namedParameters, PreparedStatement::execute);
    }

    public void updateUser(User user) {
        String sql = "UPDATE users SET first_name=:firstName, last_name=:lastName, username=:username, " +
                "password=:password, phone=:phone, country_id=:countryId WHERE id=:id";
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Country userCountry = user.getCountry();
        SqlParameterSource namedParameters = new MapSqlParameterSource().
                addValue("firstName", user.getFirstName()).
                addValue("lastName", user.getLastName()).
                addValue("username", user.getUsername()).
                addValue("password", user.getPassword()).
                addValue("phone", userCountry.getCode() + user.getTelephoneNumber()).
                addValue("countryId", userCountry.getId()).
                addValue("id", user.getId());

        namedParameterJdbcTemplate.execute(sql, namedParameters, PreparedStatement::execute);
    }

    public void updateUserWithoutPassword(User user) {
        String sql = "UPDATE users SET first_name=:firstName, last_name=:lastName, username=:username, " +
                "phone=:phone, country_id=:countryId WHERE id=:id";
        Country userCountry = user.getCountry();
        SqlParameterSource namedParameters = new MapSqlParameterSource().
                addValue("firstName", user.getFirstName()).
                addValue("lastName", user.getLastName()).
                addValue("username", user.getUsername()).
                addValue("phone", userCountry.getCode() + user.getTelephoneNumber()).
                addValue("countryId", userCountry.getId()).
                addValue("id", user.getId());

        namedParameterJdbcTemplate.execute(sql, namedParameters, PreparedStatement::execute);
    }

    public void updateUserStatus(User user) {
        String sql = "UPDATE users SET status=:status WHERE id=:id";
        SqlParameterSource namedParameters = new MapSqlParameterSource().
                addValue("status", user.getStatus().toString()).
                addValue("id", user.getId());

        namedParameterJdbcTemplate.execute(sql, namedParameters, PreparedStatement::execute);
    }

    public User findByUsername(String username) {
        try {
            String sql = "SELECT * FROM users JOIN country c on c.id = users.country_id WHERE username=:username";
            SqlParameterSource namedParameters = new MapSqlParameterSource("username", username);
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new UserWithCountryRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException(username);
        }
    }

    public List<User> findUsersByUsername(String username) {
        String sql = "SELECT * FROM users JOIN country c on c.id = users.country_id WHERE username=:username";
        SqlParameterSource namedParameters = new MapSqlParameterSource("username", username);
        return namedParameterJdbcTemplate.query(sql, namedParameters, new UserWithCountryRowMapper());
    }

    public User findByActivationCode(String activationCode) {
        try {
            String sql = "SELECT * FROM users WHERE activation_code=:activationCode";
            SqlParameterSource namedParameters = new MapSqlParameterSource("activationCode", activationCode);
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("No such user");
        }
    }

    public User getUserByTrainer(Trainer trainer) {
        try {
            String sql = "SELECT * FROM users JOIN trainer t on users.id = t.user_id" +
                    " JOIN country c on c.id = users.country_id WHERE t.id=:id";
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", trainer.getId());
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new UserWithCountryRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("No such user");
        }
    }
}
