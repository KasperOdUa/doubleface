package com.example.doubleface.repository;


import com.example.doubleface.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query(value = "SELECT * FROM message WHERE `from_id` = :personId", nativeQuery = true)
    List<Message> findAllMessagesById(@Param("personId") int personId);

    @Query(value = "SELECT * FROM message WHERE `to_id` = :personId", nativeQuery = true)
    List<Message> findAllMessagesByIdSecond(@Param("personId") int personId);
}