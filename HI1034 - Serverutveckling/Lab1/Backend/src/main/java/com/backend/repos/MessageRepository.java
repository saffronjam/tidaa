package com.backend.repos;

import com.backend.models.Message;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Override
    <S extends Message> S save(S entity);

    @Override
    List<Message> findAll();
}