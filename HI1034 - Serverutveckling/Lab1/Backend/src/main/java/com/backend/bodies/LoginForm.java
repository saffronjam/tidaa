package com.backend.bodies;


public record LoginForm(String usernameOrEmail, String password) {
    public String toJson() {
        return "{\"usernameOrEmail\": \"" + this.usernameOrEmail() + "\","
                + "\"password\": \"" + this.password() + "\"}";
    }
}
