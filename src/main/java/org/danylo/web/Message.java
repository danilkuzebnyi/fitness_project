package org.danylo.web;

public enum Message {
    WORKING_TIME_SHOULD_BE_DEFINED("You should specify both hours from and hours to"),
    TIME_VALIDATION("Hours from must be less than hours to"),
    ENTER_VALID_PHONE("Please enter valid telephone number"),
    CHECK_EMAIL("The activation link was sent to your email"),
    ALREADY_RATE_TRAINER("You have already rated this trainer"),
    CANNOT_RATE_TRAINER("To rate a trainer you need to be his client");

    private final String message;

    Message(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
