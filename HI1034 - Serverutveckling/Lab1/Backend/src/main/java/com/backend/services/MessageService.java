package com.backend.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.backend.BackendException;
import com.backend.bodies.ImageForm;
import com.backend.bodies.MessageForm;
import com.backend.models.Image;
import com.backend.models.Message;
import com.backend.models.viewmodels.ConvertHelpers;
import com.backend.models.viewmodels.MessageVm;
import com.backend.repos.MessageRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final MessageRepository messageRepo;
    private final UserService userService;
    private final ChatService chatService;
    private final ImageService imageService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MessageService(MessageRepository messageRepo, UserService userService, ChatService chatService, ImageService imageService) {
        this.messageRepo = messageRepo;
        this.userService = userService;
        this.chatService = chatService;
        this.imageService = imageService;
    }

    public void add(MessageForm form) {
        try {
            var chat = chatService.getFull(form.chatId());
            var sender = userService.getFull(form.token());
            var receiverId = sender.getId().equals(chat.getUser1().getId()) ? chat.getUser1().getId() : chat.getUser2().getId();
            var receiver = userService.getFull(receiverId);

            var now = Timestamp.valueOf(LocalDateTime.now());
            long imageId = form.image() == null ? -1 : imageService.add(new ImageForm(form.image()));
            var newMessage = new Message(form.content(), imageId, sender, receiver, chat, now);
            messageRepo.save(newMessage);

            chat.addMessage(newMessage);
            chat.setLastMessageSent(newMessage.getSent());
            chatService.save(chat);
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to create message", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}