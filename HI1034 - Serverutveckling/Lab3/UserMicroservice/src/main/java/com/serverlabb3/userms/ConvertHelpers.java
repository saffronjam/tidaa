package com.serverlabb3.userms;

public class ConvertHelpers {
    public static UserVm toUserVm(User user) {
        return new UserVm(user.getId(), user.getUsername(), user.getEmail());
    }

    public static AuthUserVm toAuthUserVm(User user) {
        return new AuthUserVm(user.getToken(), user.getId(), user.getUsername(), user.getEmail());
    }
}
