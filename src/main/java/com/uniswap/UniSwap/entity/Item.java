// src/main/java/com/uniswap/UniSwap/entity/Item.java
package com.uniswap.UniSwap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId;

    @Column(length = 100, nullable = false)
    private String itemName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(length = 50, nullable = false)
    private String itemType; // swap, sell, buy

    @Column(nullable = false)
    private LocalDate postDate;

    @Column(length = 50, nullable = false)
    private String itemCondition; // good, fair, excellent, etc.

    @Column(length = 50, nullable = false)
    private String status; // available, exchanged, etc.

    @Column(length = 20, nullable = false)
    private String phone;

    @Column(length = 200)
    private String swapWith; // only for swap items

    @Column(length = 100)
    private String department; // CSE, EEE, etc.

    // Store just the filename (e.g., "20250916_123456_abcd1234.jpg")
    @Column(length = 500)
    private String imageData;

    @Column
    private LocalDateTime createdAt;

    // Basic category and location as simple strings for now
    @Column(length = 50)
    private String category;

    @Column(length = 100)
    private String location;

    // User relationship - simplified
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (postDate == null) postDate = LocalDate.now();
    }
}
