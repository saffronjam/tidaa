package com.serverlabb3.utilities.forms;

import java.util.Collection;

public record ChatForm(String token, String name, Collection<Long> members) {
}
