package com.backend.models;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Table(name = "post")
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "created", nullable = false)
    private Timestamp created;

    @Column(name = "image_id")
    private Long imageId;

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }


    public Post() {
    }

    public Post(String content, User creator, Timestamp created, Long imageId) {
        this.content = content;
        this.creator = creator;
        this.created = created;
        this.imageId = imageId;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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


}