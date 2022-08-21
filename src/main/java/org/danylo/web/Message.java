package org.danylo.web;

public enum Message {
    WORKING_TIME_SHOULD_BE_DEFINED("You should specify both hours from and hours to"),
    TIME_VALIDATION("Hours from must be less than hours to");

    private final String message;

    Message(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
