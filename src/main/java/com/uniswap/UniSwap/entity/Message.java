// src/main/java/com/uniswap/UniSwap/entity/Message.java
package com.uniswap.UniSwap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Table(name = "Messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime sentTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // Simple reference fields instead of complex relationships
    @Column(length = 50)
    private String concernType; // item, tuition, general

    @Column
    private Integer concernId; // ID of the item/tuition being discussed

    @PrePersist
    void onCreate() {
        if (sentTime == null) sentTime = LocalDateTime.now();
    }
}
