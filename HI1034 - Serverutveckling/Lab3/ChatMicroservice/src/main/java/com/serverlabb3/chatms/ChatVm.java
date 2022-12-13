package com.serverlabb3.chatms;

import java.util.List;
import java.util.Set;

public record ChatVm(String name, long id, Set<Long> members, List<MessageVm> messages) {
}
