package com.backend.repos;

import com.backend.models.Chat;
import com.backend.models.User;
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

    @Query("select case when (count(c)> 0) then true else false end from Chat c where " +
            "(c.user1.id = :user1Id or c.user2.id = :user1Id) and " +
            "(c.user2.id = :user2Id or c.user1.id = :user2Id)")
    boolean existsByUser1AndUser2(@Param("user1Id") long id1, @Param("user2Id") long id2);

    @Query("select distinct c from Chat c where (c.user1.id = :userId1 and c.user2.id = :userId2) or (c.user2.id = :userId1 and c.user1.id = :userId2)")
    Optional<Chat> findByUsers(Long userId1, Long userId2);
}