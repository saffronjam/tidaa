package com.backend.models.viewmodels;

import java.time.LocalDateTime;

public record MessageVm(String content, Long imageId, UserVm sender, LocalDateTime sent){}
