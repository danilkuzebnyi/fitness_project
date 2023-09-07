package org.danylo;

import org.danylo.config.WebSecurityConfiguration;
import org.danylo.service.UserService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
@Order(1)
public class IntegrationTestWebSecurityConfiguration extends WebSecurityConfiguration {

    public IntegrationTestWebSecurityConfiguration(UserService userService, PasswordEncoder passwordEncoder) {
        super(userService, passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.csrf().disable();
    }
}
