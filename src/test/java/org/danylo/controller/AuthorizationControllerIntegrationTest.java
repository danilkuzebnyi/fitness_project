package org.danylo.controller;

import org.danylo.IntegrationTestWebSecurityConfiguration;
import org.danylo.model.User;
import org.danylo.service.RecaptchaService;
import org.danylo.utils.UserBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.*;
import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestWebSecurityConfiguration.class)
public class AuthorizationControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private RecaptchaService recaptchaService;

    @Test
    void getLogInForm() {
        ResponseEntity<String> response = restTemplate.getForEntity("/login", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void redirectToLoginIfPageNeedsAuthorization() {
        ResponseEntity<String> response = restTemplate.getForEntity("/profile", String.class);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals("/login", Objects.requireNonNull(response.getHeaders().getLocation()).getPath());
    }

    @Test
    void login_Success_IfUserExists() {
        ResponseEntity<String> response = executePostLoginMethod(UserBuilder.EXISTED_USERNAME,
                UserBuilder.EXISTED_PASSWORD);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals("/trainers", Objects.requireNonNull(response.getHeaders().getLocation()).getPath());
    }

    @Test
    void login_Fail_IfWrongCredentials() {
        ResponseEntity<String> response = executePostLoginMethod(UserBuilder.NON_EXISTED_USERNAME,
                UserBuilder.NON_EXISTED_PASSWORD);

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        URI location = response.getHeaders().getLocation();
        assert location != null;
        assertEquals("/login?error", location.getPath() + "?" + location.getQuery());
    }

    @Test
    void getRegisterInForm() {
        ResponseEntity<String> response = restTemplate.getForEntity("/signup", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Sql(value = {"/sql/restart-users-id-seq.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/delete-user.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void registerIn_Success() {
        ResponseEntity<String> response = executePostSignupMethod(UserBuilder.buildNonExistedUser());

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals("/login", Objects.requireNonNull(response.getHeaders().getLocation()).getPath());
    }

    @Test
    void registerIn_Fail_WhenUsernameExists() {
        ResponseEntity<String> response = executePostSignupMethod(UserBuilder.buildExistedUser());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("An account already exists for this email"));
    }

    @Test
    void registerIn_Fail_WhenPasswordIsWrong() {
        ResponseEntity<String> response = executePostSignupMethod(UserBuilder.buildNonExistedUserWithWrongPassword());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Password must be at least 4 characters"));
    }

    @Test
    void registerIn_Fail_WhenTelephoneNumberIsWrong() {
        ResponseEntity<String> response = executePostSignupMethod(UserBuilder.buildNonExistedUserWithWrongTelephoneNumber());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Please enter a valid telephone number"));
    }

    @Test
    void registerIn_Fail_WhenCountryIsNotChosen() {
        ResponseEntity<String> response = executePostSignupMethod(UserBuilder.buildNonExistedUserWithoutCountry());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Please select your country"));
    }

    @Test
    void getActivationCode() {
        String code = UUID.randomUUID().toString();
        ResponseEntity<String> response = restTemplate.getForEntity("/activation/" + code, String.class);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals("/login", Objects.requireNonNull(response.getHeaders().getLocation()).getPath());
    }

    ResponseEntity<String> executePostLoginMethod(String username, String password) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", username);
        formData.add("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        return restTemplate.exchange("/login", HttpMethod.POST, requestEntity, String.class);
    }

    ResponseEntity<String> executePostSignupMethod(User user) {
        ResponseEntity<String> response = restTemplate.getForEntity(user.getCountry() == null
                ? "/signup" : "/signup?countryId=" + user.getCountry().getId(), String.class);
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("firstName", user.getFirstName());
        formData.add("lastName", user.getLastName());
        formData.add("telephoneNumber", user.getTelephoneNumber());
        formData.add("username", user.getUsername());
        formData.add("password", user.getPassword());
        formData.add("g-recaptcha-response", UUID.randomUUID().toString());

        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.COOKIE, cookies != null ? cookies : Collections.emptyList());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        when(recaptchaService.validateResponse(anyString())).thenReturn(true);
        return restTemplate.exchange("/signup", HttpMethod.POST, requestEntity, String.class);
    }
}
