// src/main/java/com/uniswap/UniSwap/dto/AuthResponse.java
package com.uniswap.UniSwap.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Integer userId;
    private String username;
    private String email;

    public AuthResponse(String token, Integer userId, String username, String email) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}
