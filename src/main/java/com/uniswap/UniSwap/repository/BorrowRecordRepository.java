// src/main/java/com/uniswap/UniSwap/repository/BorrowRecordRepository.java
package com.uniswap.UniSwap.repository;

import com.uniswap.UniSwap.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Integer> {
    List<BorrowRecord> findByLenderUserId(Integer lenderId);
    List<BorrowRecord> findByBorrowerUserId(Integer borrowerId);
    List<BorrowRecord> findByItemItemId(Integer itemId);
    List<BorrowRecord> findByBorrowStatus(String status);

    @Query("SELECT br FROM BorrowRecord br WHERE br.returnDue < :date AND br.actualReturn IS NULL")
    List<BorrowRecord> findOverdueItems(@Param("date") LocalDate date);
}
