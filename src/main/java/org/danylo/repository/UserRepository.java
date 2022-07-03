package org.danylo.repository;

import org.danylo.model.Trainer;
import org.danylo.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class UserRepository extends BaseRepository {
    public void saveUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, username, password, phone, role) " +
                "VALUES(:firstName, :lastName, :username, :password, :phone, :role)";
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        SqlParameterSource namedParameters = new MapSqlParameterSource().
                addValue("firstName", user.getFirstName()).
                addValue("lastName", user.getLastName()).
                addValue("username", user.getUsername()).
                addValue("password", user.getPassword()).
                addValue("phone", user.getTelephoneNumber()).
                addValue("role", user.getRole().toString());

        namedParameterJdbcTemplate.execute(sql, namedParameters, PreparedStatement::execute);
    }

    public void updateUser(User user) {
        String sql = "UPDATE users SET first_name=:firstName, last_name=:lastName, username=:username, " +
                "password=:password, phone=:phone WHERE id=:id";
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        SqlParameterSource namedParameters = new MapSqlParameterSource().
                addValue("firstName", user.getFirstName()).
                addValue("lastName", user.getLastName()).
                addValue("username", user.getUsername()).
                addValue("password", user.getPassword()).
                addValue("phone", user.getTelephoneNumber()).
                addValue("id", user.getId());

        namedParameterJdbcTemplate.execute(sql, namedParameters, PreparedStatement::execute);
    }

    public List<User> findAll() {
        try {
            String sql = "SELECT * FROM users";
            return namedParameterJdbcTemplate.query(sql, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("No users in database");
        }
    }

    public User findByUsername(String username) {
        try {
            String sql = "SELECT * FROM users WHERE username=:username";
            SqlParameterSource namedParameters = new MapSqlParameterSource("username", username);
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException(username);
        }
    }

    public List<User> findUsersByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=:username";
        SqlParameterSource namedParameters = new MapSqlParameterSource("username", username);
        return namedParameterJdbcTemplate.query(sql, namedParameters, new UserRowMapper());
    }

    public User getUserByTrainer(Trainer trainer) {
        try {
            String sql = "SELECT * FROM users JOIN trainer t on users.id = t.user_id WHERE t.id=:id";
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", trainer.getId());
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("No such user");
        }
    }
}
