package com.serverlabb3.postms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.creatorId = :userId order by p.created desc")
    List<Post> findAllByUserId(Long userId);

    @Query("select p from Post p where p.creatorId in (:userIds) order by p.created desc")
    List<Post> findAllByUserIds(Long[] userIds);
}