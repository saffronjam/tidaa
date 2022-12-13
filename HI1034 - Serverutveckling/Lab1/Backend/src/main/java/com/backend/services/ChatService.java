package com.backend.services;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.backend.BackendException;
import com.backend.bodies.ChatForm;
import com.backend.models.Chat;
import com.backend.models.Message;
import com.backend.models.viewmodels.ChatPreviewVm;
import com.backend.models.viewmodels.ChatVm;
import com.backend.models.viewmodels.ConvertHelpers;
import com.backend.repos.ChatRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatRepository chatRepo;
    private final UserService userService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ChatService(ChatRepository chatRepository, UserService userService) {
        this.chatRepo = chatRepository;
        this.userService = userService;
    }

    public ChatVm get(long id) {
        return ConvertHelpers.toChatVm(getFull(id), id);
    }

    public ChatVm get(String token, long friendId) {
        try {
            var userId1 = userService.get(token).id();
            var chatOpt = chatRepo.findByUsers(userId1, friendId);
            if (chatOpt.isEmpty()) {
                throw new BackendException("Chat not found", HttpStatus.NOT_FOUND);
            }
            chatOpt.get().getMessages().sort(Comparator.comparing(Message::getSent));
            return ConvertHelpers.toChatVm(chatOpt.get(), userId1);
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Chat getFull(long id) {
        try {
            var chatOpt = chatRepo.findById(id);
            if (chatOpt.isEmpty()) {
                throw new BackendException("Chat not found", HttpStatus.NOT_FOUND);
            }
            return chatOpt.get();
        } catch (DataAccessException sqlException) {
            throw new BackendException("Failed to fetch chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ChatPreviewVm> getPreviewsByUserToken(String token) {
        try {
            var user = userService.getFull(token);
            return user.getChats().stream().map(c -> ConvertHelpers
                            .toChatPreviewVm(c, user.getId())).sorted((c1, c2) -> c2.lastMessageSent().compareTo(c1.lastMessageSent()))
                    .collect(Collectors.toList());
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to get previews", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public void add(String token, long friendId) throws BackendException {
        try {
            var user1 = userService.getFull(token);
            var user2 = userService.getFull(friendId);

            var newChat = new Chat(user1, user2);

            user1.addChat(newChat);
            user2.addChat(newChat);

            chatRepo.save(newChat);

            userService.save(user1);
            userService.save(user2);
        } catch (DataAccessException exception) {
            throw new BackendException("Failed to add chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ChatVm add(ChatForm chatForm) throws BackendException {
        try {
            var user1 = userService.getFull(chatForm.token());
            var user2 = userService.getFull(chatForm.friendId());

            if (chatRepo.existsByUser1AndUser2(user1.getId(), user2.getId())) {
                throw new BackendException("Failed to create chat. It already exists", HttpStatus.BAD_REQUEST);
            }

            var newChat = new Chat(user1, user2);
            chatRepo.save(newChat);
            return ConvertHelpers.toChatVm(newChat, user1.getId());

        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to create chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void save(Chat chat) {
        try {
            chatRepo.save(chat);
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to save chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void remove(Chat chat) {
        try {
            chatRepo.delete(chat);
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to remove chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void remove(long userId, long friendId) {
        var chatOpt = chatRepo.findByUsers(userId, friendId);
        if (chatOpt.isEmpty()) {
            throw new BackendException("Chat not found", HttpStatus.BAD_REQUEST);
        }
        remove(chatOpt.get());
    }
}