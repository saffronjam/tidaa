package com.serverlabb2.postms;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "created", nullable = false)
    private Timestamp created;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "reports_id", length = 512)
    private String reportsId;

    public String getReportsId() {
        return reportsId;
    }

    public void setReportsId(String reportsId) {
        this.reportsId = reportsId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

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

    public Post(String content, Long creatorId, Timestamp created, Long imageId, String reportsId) {
        this.content = content;
        this.creatorId = creatorId;
        this.created = created;
        this.imageId = imageId;
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


}