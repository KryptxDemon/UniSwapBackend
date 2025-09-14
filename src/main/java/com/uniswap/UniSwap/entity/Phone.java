// src/main/java/com/uniswap/UniSwap/entity/Phone.java
package com.uniswap.UniSwap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "phone") // match your actual table name
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Phone {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // existing FK column
    private User user;

    @Id
    @Column(name = "phone_number", length = 20) // use existing unique column as PK
    private String phoneNumber;
}
