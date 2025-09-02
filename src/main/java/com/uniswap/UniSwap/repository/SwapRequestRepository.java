package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.SwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SwapRequestRepository extends JpaRepository<SwapRequest, Integer> {
    List<SwapRequest> findByUserUserId(Integer userId);
    List<SwapRequest> findByStatus(String status);
    List<SwapRequest> findByItemItemId(Integer itemId);
    List<SwapRequest> findByTuitionTuitionId(Integer tuitionId);
}