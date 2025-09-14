// src/main/java/com/uniswap/UniSwap/entity/Tuition.java
package com.uniswap.UniSwap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

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

    private Integer salary;

    private Integer daysWeek;

    @Column(length = 50)
    private String clazz;

    @Column(length = 100)
    private String subject;

    @Column(length = 50)
    private String tStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "tuition", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SwapRequest> swapRequests = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "Message_Concerns_Tuition",
            joinColumns = @JoinColumn(name = "tuition_id"),
            inverseJoinColumns = @JoinColumn(name = "message_id")
    )
    private List<Message> messages = new ArrayList<>();
}
