package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findBySenderUserId(Integer senderId);
    List<Message> findByReceiverUserId(Integer receiverId);
    
    @Query("SELECT m FROM Message m WHERE (m.sender.userId = :userId1 AND m.receiver.userId = :userId2) OR (m.sender.userId = :userId2 AND m.receiver.userId = :userId1) ORDER BY m.sentTime")
    List<Message> findConversation(Integer userId1, Integer userId2);
}
