package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByStudentId(String studentId);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByStudentId(String studentId);
    boolean existsByUsername(String username);
}