package com.serverlabb2.chatms;

import java.time.LocalDateTime;

public record ChatPreviewVm(Long id, String name, LocalDateTime lastMessageSent) {
}
