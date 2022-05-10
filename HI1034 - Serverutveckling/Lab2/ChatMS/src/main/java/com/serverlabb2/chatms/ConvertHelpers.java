package com.serverlabb2.chatms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConvertHelpers {
    public static MessageVm toMessageVm(Message message) {
        return new MessageVm(
                message.getContent(),
                message.getImageId(),
                message.getSenderId(),
                message.getSent().toLocalDateTime(),
                message.getReportsId());
    }

    public static ChatVm toChatVm(Chat chat) {
        List<MessageVm> messages;
        if (chat.getMessages() != null) {
            messages = chat.getMessages().stream().map(ConvertHelpers::toMessageVm).collect(Collectors.toList());
        } else {
            messages = new ArrayList<>();
        }

        return new ChatVm(chat.getName(), chat.getId(), chat.getMembers(), messages);
    }

    public static ChatPreviewVm toChatPreviewVm(Chat chat) {
        return new ChatPreviewVm(chat.getId(), chat.getName(), chat.getLastMessageSent().toLocalDateTime());
    }
}
