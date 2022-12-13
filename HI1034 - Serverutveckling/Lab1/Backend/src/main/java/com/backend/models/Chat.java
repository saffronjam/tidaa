package com.backend.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "chat")
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "chat_id")
    private List<Message> messages;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "user_1_id", nullable = false)
    private User user1;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "user_2_id", nullable = false)
    private User user2;

    @Column(name = "last_message_sent", nullable = false)
    private Timestamp lastMessageSent;

    public Timestamp getLastMessageSent() {
        return lastMessageSent;
    }

    public void setLastMessageSent(Timestamp lastMessageSent) {
        this.lastMessageSent = lastMessageSent;
    }

    public Chat() {
    }

    public Chat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        this.messages = new ArrayList<>();
        this.lastMessageSent = Timestamp.valueOf(LocalDateTime.now());
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }


}