// src/main/java/com/uniswap/UniSwap/repository/PhoneRepository.java
package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, String> { // ID type is now String
    List<Phone> findByUserUserId(Integer userId);
    Optional<Phone> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
}
