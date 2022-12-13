package com.backend.models.viewmodels;

import java.util.Date;

public record PostVm(long id, String content, UserVm creator, Date created, Long imageId) {
}
