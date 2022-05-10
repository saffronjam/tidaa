package com.serverlabb2.chatms;

import java.time.LocalDateTime;

public record MessageVm(String content, Long imageId, Long senderId, LocalDateTime sent, String reportsId){}
