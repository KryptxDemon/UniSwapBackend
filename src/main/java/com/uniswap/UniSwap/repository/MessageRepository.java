// src/main/java/com/uniswap/UniSwap/repository/MessageRepository.java
package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findBySenderUserId(Integer senderId);
    List<Message> findByReceiverUserId(Integer receiverId);

    @Query("SELECT m FROM Message m WHERE (m.sender.userId = :senderId AND m.receiver.userId = :receiverId) OR (m.sender.userId = :receiverId AND m.receiver.userId = :senderId) ORDER BY m.sentTime")
    List<Message> findConversationBetweenUsers(@Param("senderId") Integer senderId, @Param("receiverId") Integer receiverId);

    @Query("SELECT m FROM Message m WHERE m.sender.userId = :userId OR m.receiver.userId = :userId2 ORDER BY m.sentTime DESC")
    List<Message> findBySenderUserIdOrReceiverUserId(@Param("userId") Integer userId, @Param("userId2") Integer userId2);
}
