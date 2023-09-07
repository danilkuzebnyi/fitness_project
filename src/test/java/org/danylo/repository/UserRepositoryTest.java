package org.danylo.repository;

import org.danylo.mapper.*;
import org.danylo.model.*;
import org.danylo.utils.UserBuilder;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings
class UserRepositoryTest {
    private static final User USER = UserBuilder.buildExistedUser();
    @InjectMocks
    private UserRepository underTest;
    @Mock
    private NamedParameterJdbcTemplate template;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ResultSet resultSet;
    @Captor
    private ArgumentCaptor<SqlParameterSource> sqlParamsCaptor;
    @Captor
    private ArgumentCaptor<UserRowMapper> userRowMapperCaptor;
    @Captor
    private ArgumentCaptor<UserWithCountryRowMapper> userWithCountryRowMapperCaptor;

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(template, passwordEncoder, resultSet);
    }

    @Test
    public void saveUser() {
        String password = UUID.randomUUID().toString();
        when(passwordEncoder.encode(USER.getPassword())).thenReturn(password);

        underTest.saveUser(USER);

        verify(template).execute(anyString(), sqlParamsCaptor.capture(), any());
        var sqlParams = sqlParamsCaptor.getValue();
        Country userCountry = USER.getCountry();

        assertEquals(USER.getFirstName(), sqlParams.getValue("firstName"));
        assertEquals(USER.getLastName(), sqlParams.getValue("lastName"));
        assertEquals(USER.getUsername(), sqlParams.getValue("username"));
        assertEquals(password, sqlParams.getValue("password"));
        assertEquals(userCountry.getCode() + USER.getTelephoneNumber(), sqlParams.getValue("phone"));
        assertEquals(USER.getRole().toString(), sqlParams.getValue("role"));
        assertEquals(userCountry.getId(), sqlParams.getValue("countryId"));
        assertEquals(USER.getActivationCode(), sqlParams.getValue("activationCode"));
        assertEquals(USER.getStatus().toString(), sqlParams.getValue("status"));
    }

    @Test
    public void updateUser() {
        String password = UUID.randomUUID().toString();
        when(passwordEncoder.encode(USER.getPassword())).thenReturn(password);

        underTest.updateUser(USER);

        verify(template).execute(anyString(), sqlParamsCaptor.capture(), any());
        var sqlParams = sqlParamsCaptor.getValue();
        Country userCountry = USER.getCountry();

        assertEquals(USER.getFirstName(), sqlParams.getValue("firstName"));
        assertEquals(USER.getLastName(), sqlParams.getValue("lastName"));
        assertEquals(USER.getUsername(), sqlParams.getValue("username"));
        assertEquals(password, sqlParams.getValue("password"));
        assertEquals(userCountry.getCode() + USER.getTelephoneNumber(), sqlParams.getValue("phone"));
        assertEquals(userCountry.getId(), sqlParams.getValue("countryId"));
        assertEquals(USER.getId(), sqlParams.getValue("id"));
    }

    @Test
    public void updateUserWithoutPassword() {
        underTest.updateUserWithoutPassword(USER);

        verify(template).execute(anyString(), sqlParamsCaptor.capture(), any());
        var sqlParams = sqlParamsCaptor.getValue();
        Country userCountry = USER.getCountry();

        assertEquals(USER.getFirstName(), sqlParams.getValue("firstName"));
        assertEquals(USER.getLastName(), sqlParams.getValue("lastName"));
        assertEquals(USER.getUsername(), sqlParams.getValue("username"));
        assertEquals(userCountry.getCode() + USER.getTelephoneNumber(), sqlParams.getValue("phone"));
        assertEquals(userCountry.getId(), sqlParams.getValue("countryId"));
        assertEquals(USER.getId(), sqlParams.getValue("id"));
    }

    @Test
    public void updateUserStatus() {
        underTest.updateUserStatus(USER);

        verify(template).execute(anyString(), sqlParamsCaptor.capture(), any());
        var sqlParams = sqlParamsCaptor.getValue();

        assertEquals(USER.getStatus().toString(), sqlParams.getValue("status"));
        assertEquals(USER.getId(), sqlParams.getValue("id"));
    }

    @Test
    public void findByUsername() throws SQLException {
        String username = USER.getUsername();
        mockResultSetForUserWithCountry();
        when(template.queryForObject(anyString(), any(SqlParameterSource.class), any(UserWithCountryRowMapper.class)))
                .thenReturn(USER);

        User actualUser = underTest.findByUsername(username);

        verify(template).queryForObject(anyString(), sqlParamsCaptor.capture(), userWithCountryRowMapperCaptor.capture());
        var sqlParams = sqlParamsCaptor.getValue();
        var userWithCountryRowMapper = userWithCountryRowMapperCaptor.getValue();
        assertEquals(USER, actualUser);
        assertEquals(username, sqlParams.getValue("username"));
        testUserWithCountryRowMapper(userWithCountryRowMapper, USER);
    }

    @Test
    public void findByUsername_ThrowUsernameNotFoundException_WhenEmptyResult() {
        String username = USER.getUsername();
        when(template.queryForObject(anyString(), any(SqlParameterSource.class), any(UserWithCountryRowMapper.class)))
                .thenThrow(EmptyResultDataAccessException.class);

        var thrownException = assertThrows(UsernameNotFoundException.class,
                () -> underTest.findByUsername(username));

        assertEquals(username, thrownException.getMessage());
        verify(template).queryForObject(anyString(), sqlParamsCaptor.capture(), userWithCountryRowMapperCaptor.capture());
        var sqlParams = sqlParamsCaptor.getValue();
        assertEquals(username, sqlParams.getValue("username"));
    }

    @Test
    public void findUsersByUsername() throws SQLException {
        String username = "some_username";
        List<User> expectedUsers = Instancio.ofList(User.class).size(2).create();
        User expectedUser1 = expectedUsers.get(0);
        User expectedUser2 = expectedUsers.get(1);
        mockResultSetForUsersWithCountry(expectedUser1, expectedUser2);
        when(template.query(anyString(), any(SqlParameterSource.class), any(UserWithCountryRowMapper.class)))
                .thenReturn(expectedUsers);

        List<User> actualUsers = underTest.findUsersByUsername(username);

        assertEquals(expectedUsers, actualUsers);
        verify(template).query(anyString(), sqlParamsCaptor.capture(), userWithCountryRowMapperCaptor.capture());
        var sqlParams = sqlParamsCaptor.getValue();
        var userWithCountryRowMapper = userWithCountryRowMapperCaptor.getValue();
        assertEquals(username, sqlParams.getValue("username"));
        testUserWithCountryRowMapper(userWithCountryRowMapper, expectedUser1);
        testUserWithCountryRowMapper(userWithCountryRowMapper, expectedUser2);
    }

    @Test
    public void findByActivationCode() throws SQLException {
        String activationCode = UUID.randomUUID().toString();
        mockResultSetForUser();
        when(template.queryForObject(anyString(), any(SqlParameterSource.class), any(UserRowMapper.class)))
                .thenReturn(USER);

        User actualUser = underTest.findByActivationCode(activationCode);

        assertEquals(USER, actualUser);
        verify(template).queryForObject(anyString(), sqlParamsCaptor.capture(), userRowMapperCaptor.capture());
        var sqlParams = sqlParamsCaptor.getValue();
        var userRowMapper = userRowMapperCaptor.getValue();
        assertEquals(activationCode, sqlParams.getValue("activationCode"));
        testUserRowMapper(userRowMapper);
    }

    @Test
    public void findByActivationCode_ThrowUsernameNotFoundException_WhenEmptyResult() {
        String activationCode = UUID.randomUUID().toString();
        when(template.queryForObject(anyString(), any(SqlParameterSource.class), any(UserRowMapper.class)))
                .thenThrow(EmptyResultDataAccessException.class);

        var thrownException = assertThrows(UsernameNotFoundException.class,
                () -> underTest.findByActivationCode(activationCode));

        assertEquals("No such user", thrownException.getMessage());
        verify(template).queryForObject(anyString(), sqlParamsCaptor.capture(), userRowMapperCaptor.capture());
        var sqlParams = sqlParamsCaptor.getValue();
        assertEquals(activationCode, sqlParams.getValue("activationCode"));
    }

    @Test
    public void getUserByTrainer() throws SQLException {
        Trainer trainer = Instancio.create(Trainer.class);
        mockResultSetForUserWithCountry();
        when(template.queryForObject(anyString(), any(SqlParameterSource.class), any(UserWithCountryRowMapper.class)))
                .thenReturn(USER);

        User actualUser = underTest.getUserByTrainer(trainer);

        verify(template).queryForObject(anyString(), sqlParamsCaptor.capture(), userWithCountryRowMapperCaptor.capture());
        var sqlParams = sqlParamsCaptor.getValue();
        var userWithCountryRowMapper = userWithCountryRowMapperCaptor.getValue();
        assertEquals(USER, actualUser);
        assertEquals(trainer.getId(), sqlParams.getValue("id"));
        testUserWithCountryRowMapper(userWithCountryRowMapper, USER);
    }

    @Test
    public void getUserByTrainer_ThrowUsernameNotFoundException_WhenEmptyResult() {
        Trainer trainer = Instancio.create(Trainer.class);
        when(template.queryForObject(anyString(), any(SqlParameterSource.class), any(UserWithCountryRowMapper.class)))
                .thenThrow(EmptyResultDataAccessException.class);

        var thrownException = assertThrows(UsernameNotFoundException.class,
                () -> underTest.getUserByTrainer(trainer));

        assertEquals("No such user", thrownException.getMessage());
        verify(template).queryForObject(anyString(), sqlParamsCaptor.capture(), userWithCountryRowMapperCaptor.capture());
        var sqlParams = sqlParamsCaptor.getValue();
        assertEquals(trainer.getId(), sqlParams.getValue("id"));
    }

    private void mockResultSetForUserWithCountry() throws SQLException {
        mockResultSetForUser();
        Country userCountry = USER.getCountry();
        when(resultSet.getInt("country_id")).thenReturn(userCountry.getId());
        when(resultSet.getString("name")).thenReturn(userCountry.getName());
        when(resultSet.getString("code")).thenReturn(userCountry.getCode());
    }

    private void mockResultSetForUser() throws SQLException {
        when(resultSet.getInt("id")).thenReturn(USER.getId());
        when(resultSet.getString("first_name")).thenReturn(USER.getFirstName());
        when(resultSet.getString("last_name")).thenReturn(USER.getLastName());
        when(resultSet.getString("username")).thenReturn(USER.getUsername());
        when(resultSet.getString("phone")).thenReturn(USER.getTelephoneNumber());
        when(resultSet.getString("password")).thenReturn(USER.getPassword());
        when(resultSet.getString("role")).thenReturn(USER.getRole().toString());
        when(resultSet.getString("status")).thenReturn(USER.getStatus().toString());
        when(resultSet.getString("activation_code")).thenReturn(USER.getActivationCode());
    }

    private void mockResultSetForUsersWithCountry(User user1, User user2) throws SQLException {
        when(resultSet.getInt("id")).thenReturn(user1.getId(), user2.getId());
        when(resultSet.getString("first_name")).thenReturn(user1.getFirstName(), user2.getFirstName());
        when(resultSet.getString("last_name")).thenReturn(user1.getLastName(), user2.getLastName());
        when(resultSet.getString("username")).thenReturn(user1.getUsername(), user2.getUsername());
        when(resultSet.getString("phone")).thenReturn(user1.getTelephoneNumber(), user2.getTelephoneNumber());
        when(resultSet.getString("password")).thenReturn(user1.getPassword(), user2.getPassword());
        when(resultSet.getString("role")).thenReturn(user1.getRole().toString(), user2.getRole().toString());
        when(resultSet.getString("status")).thenReturn(user1.getStatus().toString(), user2.getStatus().toString());
        when(resultSet.getString("activation_code")).thenReturn(user1.getActivationCode(), user2.getActivationCode());
        when(resultSet.getInt("country_id")).thenReturn(user1.getCountry().getId(), user2.getCountry().getId());
        when(resultSet.getString("name")).thenReturn(user1.getCountry().getName(), user2.getCountry().getName());
        when(resultSet.getString("code")).thenReturn(user1.getCountry().getCode(), user2.getCountry().getCode());
    }

    private void testUserWithCountryRowMapper(UserWithCountryRowMapper userWithCountryRowMapper, User expectedUser)
            throws SQLException {
        User userFromDb = userWithCountryRowMapper.mapRow(resultSet, -1);
        assert userFromDb != null;
        assertUserEquals(expectedUser, userFromDb);
        assertEquals(expectedUser.getCountry().getId(), userFromDb.getCountry().getId());
        assertEquals(expectedUser.getCountry().getName(), userFromDb.getCountry().getName());
        assertEquals(expectedUser.getCountry().getCode(), userFromDb.getCountry().getCode());
    }

    private void testUserRowMapper(UserRowMapper userRowMapper) throws SQLException {
        User userFromDb = userRowMapper.mapRow(resultSet, -1);
        assert userFromDb != null;
        assertUserEquals(USER, userFromDb);
    }

    private void assertUserEquals(User user1, User user2) {
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getFirstName(), user2.getFirstName());
        assertEquals(user1.getLastName(), user2.getLastName());
        assertEquals(user1.getUsername(), user2.getUsername());
        assertEquals(user1.getTelephoneNumber(), user2.getTelephoneNumber());
        assertEquals(user1.getPassword(), user2.getPassword());
        assertEquals(user1.getRole().toString(), user2.getRole().toString());
        assertEquals(user1.getStatus().toString(), user2.getStatus().toString());
        assertEquals(user1.getActivationCode(), user2.getActivationCode());
    }
}