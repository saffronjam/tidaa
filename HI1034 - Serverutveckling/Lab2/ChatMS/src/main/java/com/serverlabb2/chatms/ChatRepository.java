package com.serverlabb2.chatms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Override
    <S extends Chat> S save(S entity);

    @Override
    List<Chat> findAll();

    @Query("select c from Chat c where :userId in (select m from c.members m)")
    List<Chat> findByUserId(Long userId); // Maybe don't work
}