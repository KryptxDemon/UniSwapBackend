// src/main/java/com/uniswap/UniSwap/entity/Tuition.java
package com.uniswap.UniSwap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Table(name = "Tuition")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Tuition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tuitionId;

    @Column(nullable = false)
    private Integer salary;

    @Column(nullable = false)
    private Integer daysWeek;

    @Column(length = 50, nullable = false)
    private String clazz;

    @Column(length = 100, nullable = false)
    private String subject;

    @Column(length = 50, nullable = false)
    private String tStatus;

    @Column
    private Boolean canSwap = false; // Whether tuition exchange is available

    @Column(columnDefinition = "TEXT")
    private String swapDetails; // Details about what they want in exchange

    @Column(length = 20, nullable = false)
    private String contactPhone; // Contact phone number

    @Column(length = 50)
    private String tutorPreference; // male, female, both

    @Column(columnDefinition = "TEXT")
    private String addressUrl; // Meeting address or Google Maps link

    @Column(length = 100)
    private String location; // Simple location string

    @Column
    private LocalDateTime createdAt;

    // User relationship
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
