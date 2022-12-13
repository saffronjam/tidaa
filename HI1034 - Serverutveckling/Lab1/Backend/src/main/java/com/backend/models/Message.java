package com.backend.models;

import javax.persistence.*;
import java.sql.Timestamp;

@Table(name = "message")
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @OrderBy
    @Column(name = "sent", nullable = false)
    private Timestamp sent;

    @Column(name = "image_id", nullable = false)
    private long imageId;

    public Timestamp getSent() {
        return sent;
    }

    public void setSent(Timestamp sent) {
        this.sent = sent;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Message() {
    }

    public Message(String content, Long imageId, User sender, User receiver, Chat chat, Timestamp sent) {
        this.content = content;
        this.imageId = imageId;
        this.sender = sender;
        this.receiver = receiver;
        this.chat = chat;
        this.sent = sent;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }
}