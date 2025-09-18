package com.uniswap.UniSwap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Integer messageId;
    private String text;
    private LocalDateTime sentTime;
    private UserSummaryDTO sender;
    private UserSummaryDTO receiver;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDTO {
        private Integer userId;
        private String displayUsername;
        private String email;
        private String profilePicture;
    }
}