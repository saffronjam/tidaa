package com.backend.repos;

import com.backend.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.creator.id = :userId order by p.created desc")
    List<Post> findAllByUserId(long userId);
}