package com.serverlabb3.userms;

public record AuthUserVm(String token, long id, String name, String email) {
}