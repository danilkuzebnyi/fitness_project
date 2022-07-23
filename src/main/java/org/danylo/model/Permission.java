package org.danylo.model;

public enum Permission {
    BOOK("book"),
    RATE("rate"),
    EDIT_USER("editUser"),
    EDIT_TRAINER("editTrainer");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
