package org.danylo.model;

import org.springframework.stereotype.Component;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Component
public class User {
    private int id;

    private String firstName;

    private String lastName;

    private String username;

    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    @Pattern(regexp = "^\\(?03[9]\\)?\\d{7}|^\\(?05[0]\\)?\\d{7}|^\\(?06[3678]\\)?\\d{7}|^\\(?07[3]\\)?\\d{7}|^\\(?09[1-9]\\)?\\d{7}",
             message = "Please enter a valid telephone number")
    private String telephoneNumber;

    private Role role = Role.USER;

    private Status status = Status.ACTIVE;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String email) {
        this.username = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
