package com.backend.models.viewmodels;

import java.util.List;

public record ChatVm(long id, UserVm friend, List<MessageVm> messages) {
}
