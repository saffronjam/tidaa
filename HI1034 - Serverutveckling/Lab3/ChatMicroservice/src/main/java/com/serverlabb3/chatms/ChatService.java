package com.serverlabb3.chatms;

import com.serverlabb3.utilities.Ip;
import com.serverlabb3.utilities.Requests;
import com.serverlabb3.utilities.bodies.IdBody;
import com.serverlabb3.utilities.forms.ChatForm;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.serverlabb3.utilities.exceptions.*;

@Service
public class ChatService {
    private final ChatRepository chatRepo;
    private final Ip ip;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ChatService(ChatRepository chatRepository, Ip ip) {
        this.chatRepo = chatRepository;
        this.ip = ip;
    }

    public ChatVm get(String token, Long id) {
        try {
            validateToken(token);

            var chatOpt = chatRepo.findById(id);
            if (chatOpt.isEmpty()) {
                throw new BackendException("Chat not found", HttpStatus.NOT_FOUND);
            }
            chatOpt.get().getMessages().sort(Comparator.comparing(Message::getSent));
            return ConvertHelpers.toChatVm(chatOpt.get());
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Long validateToken(String token) {
        // Validate token
        var userIdBody = Requests.getJsonObject(Requests.createGet(ip.getUserMs() + "/validate/" + token), IdBody.class);
        if (userIdBody.id() == -1) {
            throw new BackendException("Invalid user token", HttpStatus.BAD_REQUEST);
        }
        return userIdBody.id();
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

    public List<ChatPreviewVm> getPreviewsByUserToken(String token) throws BackendException {
        try {
            var userId = validateToken(token);
            var chats = chatRepo.findByUserId(userId);
            return chats.stream().map(ConvertHelpers::toChatPreviewVm).collect(Collectors.toList());
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to get previews", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public void add(ChatForm chatForm) throws BackendException {
        try {
            validateToken(chatForm.token());
            var newChat = new Chat(chatForm.name(), new HashSet<>(chatForm.members()));
            chatRepo.save(newChat);
        } catch (DataAccessException exception) {
            throw new BackendException("Failed to add chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void save(Chat chat) {
        try {
            chatRepo.save(chat);
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to save chat", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}