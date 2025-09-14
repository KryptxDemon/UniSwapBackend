// src/main/java/com/uniswap/UniSwap/entity/Message.java
package com.uniswap.UniSwap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(columnDefinition = "TEXT")
    private String text;

    private LocalDateTime sentTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    @JsonIgnore
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    @JsonIgnore
    private User receiver;

    @ManyToMany(mappedBy = "messages")
    @JsonIgnore
    private List<Item> items = new ArrayList<>();

    @ManyToMany(mappedBy = "messages")
    @JsonIgnore
    private List<Tuition> tuitions = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (sentTime == null) sentTime = LocalDateTime.now();
    }
}
