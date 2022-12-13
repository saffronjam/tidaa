package com.backend.models.viewmodels;

import java.time.LocalDateTime;

public record ChatPreviewVm(UserVm friend, LocalDateTime lastMessageSent) {
}
