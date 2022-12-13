package com.serverlabb2.userms;

public record AuthUserVm(String token, long id, String name, String email) {
}