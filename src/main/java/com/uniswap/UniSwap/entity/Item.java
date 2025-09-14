// src/main/java/com/uniswap/UniSwap/entity/Item.java
package com.uniswap.UniSwap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Column(length = 100)
    private String itemName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String itemType;

    private LocalDate postDate;

    @Column(length = 50)
    private String itemCondition;

    @Column(length = 50)
    private String status;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String swapWith;

    @Column(length = 100)
    private String department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<SwapRequest> swapRequests = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    @ManyToMany(mappedBy = "items")
    @JsonIgnore
    private List<Wishlist> wishlists = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "Message_Concerns_Item",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "message_id")
    )
    @JsonIgnore
    private List<Message> messages = new ArrayList<>();
}
