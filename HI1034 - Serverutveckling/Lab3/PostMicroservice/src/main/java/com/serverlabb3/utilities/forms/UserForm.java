package com.serverlabb3.utilities.forms;

public record UserForm(String email, String username, String password) {
    public String toJson() {
        return "{\"email\": \"" + this.email + "\","
                + "\"username\": \"" + this.username + "\","
                + "\"password\": \"" + this.password + "\"}";
    }
}
