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
    
    @Query("SELECT m FROM Message m WHERE (m.sender.userId = :senderId AND m.receiver.userId = :receiverId) OR (m.sender.userId = :receiverId AND m.receiver.userId = :senderId) ORDER BY m.sentTime")
    List<Message> findConversationBetweenUsers(Integer senderId, Integer receiverId);
    
    @Query("SELECT m FROM Message m WHERE m.sender.userId = :userId OR m.receiver.userId = :userId ORDER BY m.sentTime DESC")
    List<Message> findBySenderUserIdOrReceiverUserId(Integer userId, Integer userId2);
}
