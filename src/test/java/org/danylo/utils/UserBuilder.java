package org.danylo.utils;

import org.danylo.model.Country;
import org.danylo.model.Role;
import org.danylo.model.Status;
import org.danylo.model.User;
import java.util.UUID;

public class UserBuilder {
    public static final String EXISTED_USERNAME = "gorin@gmail.com";
    public static final String EXISTED_PASSWORD= "gorin@gmail.com";
    public static final String NON_EXISTED_USERNAME = "nouser@gmail.com";
    public static final String NON_EXISTED_PASSWORD= "nouser";

    public static User buildExistedUser() {
        User user = buildUser();
        user.setUsername(EXISTED_USERNAME);
        user.setPassword(EXISTED_PASSWORD);

        return user;
    }

    public static User buildNonExistedUser() {
        User user = buildUser();
        user.setUsername(NON_EXISTED_USERNAME);
        user.setPassword(NON_EXISTED_PASSWORD);

        return user;
    }

    public static User buildUser() {
        User user = new User();
        user.setFirstName("Max");
        user.setLastName("Shevchenko");
        Country country = new Country();
        country.setId(225);
        country.setName("Ukraine");
        country.setCode("380");
        user.setCountry(country);
        user.setTelephoneNumber("678888888");
        user.setRole(Role.USER);
        user.setActivationCode(UUID.randomUUID().toString());
        user.setStatus(Status.ACTIVE);

        return user;
    }
}
