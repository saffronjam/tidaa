package com.backend.models.viewmodels;

public record AuthUserVm(String token, long id, String name, String email) {
}