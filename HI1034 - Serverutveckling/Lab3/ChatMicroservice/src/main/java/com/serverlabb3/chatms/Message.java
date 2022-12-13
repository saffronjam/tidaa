package com.serverlabb3.chatms;

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

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @OrderBy
    @Column(name = "sent", nullable = false)
    private Timestamp sent;

    @Column(name = "image_id", nullable = false)
    private long imageId;



    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "reports_id", length = 512)
    private String reportsId;

    public String getReportsId() {
        return reportsId;
    }

    public void setReportsId(String reportsId) {
        this.reportsId = reportsId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Timestamp getSent() {
        return sent;
    }

    public void setSent(Timestamp sent) {
        this.sent = sent;
    }

    public Message() {
    }

    public Message(String content, Long imageId, Long senderId, Chat chat, Timestamp sent, String reportsId) {
        this.content = content;
        this.imageId = imageId;
        this.senderId = senderId;
        this.chat = chat;
        this.sent = sent;
        this.reportsId = reportsId;
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