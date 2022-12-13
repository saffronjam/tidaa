package com.serverlabb3.chatms;

import java.time.LocalDateTime;

public record ChatPreviewVm(Long id, String name, LocalDateTime lastMessageSent) {
}
