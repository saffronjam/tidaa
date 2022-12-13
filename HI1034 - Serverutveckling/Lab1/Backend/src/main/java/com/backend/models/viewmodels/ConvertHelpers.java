package com.backend.models.viewmodels;

import com.backend.models.Chat;
import com.backend.models.Message;
import com.backend.models.Post;
import com.backend.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConvertHelpers {
    public static UserVm toUserVm(User user) {
        return new UserVm(user.getId(), user.getUsername(), user.getEmail());
    }

    public static AuthUserVm toAuthUserVm(User user) {
        return new AuthUserVm(user.getToken(), user.getId(), user.getUsername(), user.getEmail());
    }

    public static MessageVm toMessageVm(Message message) {
        return new MessageVm(message.getContent(), message.getImageId(), toUserVm(message.getSender()), message.getSent().toLocalDateTime());
    }

    public static ChatVm toChatVm(Chat chat, Long userId) {
        var friend = ConvertHelpers.toUserVm(userId.equals(chat.getUser1().getId()) ? chat.getUser2() : chat.getUser1());

        List<MessageVm> messages;
        if (chat.getMessages() != null) {
            messages = chat.getMessages().stream().map(ConvertHelpers::toMessageVm).collect(Collectors.toList());
        } else {
            messages = new ArrayList<>();
        }

        return new ChatVm(chat.getId(), friend, messages);
    }

    public static ChatPreviewVm toChatPreviewVm(Chat chat, Long userId) {
        var user = ConvertHelpers.toUserVm(userId.equals(chat.getUser1().getId()) ? chat.getUser2() : chat.getUser1());
        return new ChatPreviewVm(user, chat.getLastMessageSent().toLocalDateTime());
    }

    public static PostVm toPostVm(Post post) {
        return new PostVm(
                post.getId(),
                post.getContent(),
                ConvertHelpers.toUserVm(post.getCreator()),
                post.getCreated(),
                post.getImageId());
    }
}
