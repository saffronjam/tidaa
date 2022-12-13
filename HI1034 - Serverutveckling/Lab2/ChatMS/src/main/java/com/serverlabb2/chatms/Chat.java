package com.serverlabb2.chatms;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Table(name = "chat")
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "last_message_sent", nullable = false)
    private Timestamp lastMessageSent;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @Column(name = "name", nullable = false)
    private String name;

    @ElementCollection
    @Column(name = "member")
    @CollectionTable(name = "chat_members", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Long> members = new LinkedHashSet<>();

    public Set<Long> getMembers() {
        return members;
    }

    public void setMembers(Set<Long> members) {
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Timestamp getLastMessageSent() {
        return lastMessageSent;
    }

    public void setLastMessageSent(Timestamp lastMessageSent) {
        this.lastMessageSent = lastMessageSent;
    }

    public Chat() {
    }

    public Chat(String name, HashSet<Long> members) {
        this.name = name;
        this.members = members;
        this.messages = new ArrayList<>();
        this.lastMessageSent = Timestamp.valueOf(LocalDateTime.now());
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