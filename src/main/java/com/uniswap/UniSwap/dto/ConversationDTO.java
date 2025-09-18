package com.uniswap.UniSwap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Integer partnerId;
    private String partnerUsername;
    private String partnerEmail;
    private String partnerProfilePicture;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer messageCount;
    private Integer unreadCount;
}