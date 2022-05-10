package com.serverlabb3.chatms;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.serverlabb3.utilities.Ip;
import com.serverlabb3.utilities.Requests;
import com.serverlabb3.utilities.bodies.IdBody;
import com.serverlabb3.utilities.bodies.IdStringBody;
import com.serverlabb3.utilities.exceptions.BackendException;
import com.serverlabb3.utilities.forms.MessageForm;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class MessageService {
    private final MessageRepository messageRepo;
    private final ChatService chatService;
    private final Ip ip;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MessageService(MessageRepository messageRepo, ChatService chatService, Ip ip) {
        this.messageRepo = messageRepo;
        this.chatService = chatService;
        this.ip = ip;
    }

    public void add(MessageForm messageForm) {
        try {
            var chat = chatService.getFull(messageForm.chatId());

            // Fetch user id
            var userIdBody = Requests.getJsonObject(Requests.createGet(ip.getUserMs() + "/validate/" + messageForm.token()), IdBody.class);
            if (userIdBody.id() == -1) {
                throw new BackendException("Invalid user token", HttpStatus.BAD_REQUEST);
            }

            // Fetch image
            long imageId = -1;
            var img = messageForm.image();
            if (messageForm.image() != null) {
                var body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(img.getName(), img.getOriginalFilename(), RequestBody.create(img.getBytes(), Requests.PNG))
                        .build();
                var imageIdBody = Requests.getJsonObject(Requests.createPost(ip.getImageMs() + "/images", body), IdBody.class);
                imageId = imageIdBody.id();
            }

            boolean hasReports = messageForm.reportsString() != null && messageForm.reportsString().length() > 2;
            String reportsId = "-1";
            if (hasReports) {
                Type listType = new TypeToken<ArrayList<Integer>>() {
                }.getType();
                var reports = new Gson().fromJson(messageForm.reportsString(), listType);
                var reportsStringBody = Requests.getJsonObject(Requests.createPost(ip.getVertxMs() + "/reportSet", RequestBody.create(new Gson().toJson(reports), Requests.JSON)), IdStringBody.class);
                reportsId = reportsStringBody.id();
            }

            var now = Timestamp.valueOf(LocalDateTime.now());
            var newMessage = new Message(messageForm.content(), imageId, userIdBody.id(), chat, now, reportsId);
            messageRepo.save(newMessage);

            chat.addMessage(newMessage);
            chat.setLastMessageSent(newMessage.getSent());
            chatService.save(chat);
        } catch (IOException | DataAccessException ignored) {
            throw new BackendException("Failed to create message", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}