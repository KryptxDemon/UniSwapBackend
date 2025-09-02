package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Optional<Admin> findByAdminEmail(String adminEmail);
}